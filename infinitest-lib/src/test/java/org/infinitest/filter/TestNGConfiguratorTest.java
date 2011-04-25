/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2011
 * "Matthias Droste" <matthias.droste@gmail.com>,
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.filter;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.infinitest.FakeEventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.TestNGConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class TestNGConfiguratorTest
{
    private static final String GROUPS = "quick";
    private static final File TEMP_FILE = new File("temp.filter");
    private static final String EXCLUDED = "slow, broken, manual";
    private static final String EXCLUDEDLINE = "## excluded-groups=" + EXCLUDED;
    private static final String GROUPSLINE = "## groups=" + GROUPS;

    private File filterFile = null;
    private RuntimeEnvironment environment;

    @Before
    public void setup() throws IOException
    {
        TestNGConfiguration.INSTANCE.clear();
        environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(), systemClasspath(),
                        currentJavaHome());
        filterFile = InfinitestCoreBuilder.getFilterFile(environment);
        appendTestNGFilter();
    }

    @After
    public void cleanup() throws IOException
    {
        Files.copy(TEMP_FILE, filterFile);
        TEMP_FILE.delete();
    }

    @Test
    public void testFilterList() throws IOException
    {
        File file = createTestFile(EXCLUDEDLINE);

        assertNull(TestNGConfiguration.INSTANCE.getExcludedGroups());
        new TestNGConfigurator(file);
        assertNotNull(TestNGConfiguration.INSTANCE.getExcludedGroups());
        assertEquals(EXCLUDED, TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    @Test
    public void testReadingIncludedGroup() throws IOException
    {
        File file = createTestFile(GROUPSLINE);
        assertNull(TestNGConfiguration.INSTANCE.getGroups());

        new TestNGConfigurator(file);
        assertNotNull(TestNGConfiguration.INSTANCE.getGroups());
        assertEquals(GROUPS, TestNGConfiguration.INSTANCE.getGroups());
    }

    @Test
    public void testReadingIncludedAndExcludedGroups() throws IOException
    {
        File file = createTestFile(GROUPSLINE, EXCLUDEDLINE);
        assertNull(TestNGConfiguration.INSTANCE.getGroups());
        assertNull(TestNGConfiguration.INSTANCE.getExcludedGroups());

        new TestNGConfigurator(file);
        assertNotNull(TestNGConfiguration.INSTANCE.getGroups());
        assertEquals(GROUPS, TestNGConfiguration.INSTANCE.getGroups());
        assertEquals(EXCLUDED, TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    @Test
    public void testEmptyGroups() throws IOException
    {
        File file = createTestFile("## excluded-groups= ");
        new TestNGConfigurator(file);
        assertNull(TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    @Test
    public void testSpacesInGroupsLine() throws IOException
    {
        final String halloGroup = "hallo";
        File file = createTestFile("##excluded-groups = " + halloGroup + " ");
        new TestNGConfigurator(file);
        assertEquals(halloGroup, TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    @Test
    public void testEmptyFile()
    {
        File file = new File("testng.config");
        assertNull(TestNGConfiguration.INSTANCE.getExcludedGroups());
        new TestNGConfigurator(file);
        assertNull(TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    @Test
    public void testReloading() throws IOException
    {
        File file = createTestFile(EXCLUDEDLINE);
        TestNGConfigurator testNGConfigurator = new TestNGConfigurator(file);
        assertNotNull(TestNGConfiguration.INSTANCE.getExcludedGroups());
        setDifferentEXCLUDEDGroup();
        testNGConfigurator.reloading();
        assertEquals(EXCLUDED, TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    @Test
    public void testReloadingInitiatedFromInfinitestCore()
    {
        InfinitestCore core = createCoreWithEXCLUDEDGroup();
        setDifferentEXCLUDEDGroup();
        core.reload();
        assertEquals(EXCLUDED, TestNGConfiguration.INSTANCE.getExcludedGroups());
    }

    private void setDifferentEXCLUDEDGroup()
    {
        TestNGConfiguration.INSTANCE.setExcludedGroups("lalala");
        assertFalse(EXCLUDED.equals(TestNGConfiguration.INSTANCE.getExcludedGroups()));
    }

    private InfinitestCore createCoreWithEXCLUDEDGroup()
    {
        InfinitestCoreBuilder builder = new InfinitestCoreBuilder(environment, new FakeEventQueue());
        InfinitestCore core = builder.createCore();
        assertEquals(EXCLUDED, TestNGConfiguration.INSTANCE.getExcludedGroups());
        return core;
    }

    private void appendTestNGFilter() throws IOException
    {
        Files.copy(filterFile, TEMP_FILE);
        final PrintWriter writer = new PrintWriter(filterFile);
        try
        {
            writer.append(EXCLUDEDLINE);
        }
        finally
        {
            writer.close();
        }
    }

    private File createTestFile(String... additionalLines) throws IOException
    {
        final File file = File.createTempFile("filter", "conf");
        file.deleteOnExit();
        final PrintWriter writer = new PrintWriter(file);
        try
        {
            writer.println("## TestNG Configuration");
            for (String line : additionalLines)
            {
                writer.println(line);
            }
            writer.println("#foo.bar");
            writer.println("Some other content");
        }
        finally
        {
            writer.close();
        }
        return file;
    }
}
