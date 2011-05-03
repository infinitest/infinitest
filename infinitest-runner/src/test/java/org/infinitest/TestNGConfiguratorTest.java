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
package org.infinitest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

public class TestNGConfiguratorTest
{
    private static final String GROUPS = "quick";
    private static final String EXCLUDED = "slow, broken, manual";
    private static final String EXCLUDEDLINE = "## excluded-groups=" + EXCLUDED;
    private static final String GROUPSLINE = "## groups=" + GROUPS;

    @Test
    public void testFilterList() throws IOException
    {
        TestNGConfiguration testNGConfiguration = new TestNGConfiguration();
        File file = createTestFile(EXCLUDEDLINE);

        assertNull(testNGConfiguration.getExcludedGroups());
        testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNotNull(testNGConfiguration.getExcludedGroups());
        assertEquals(EXCLUDED, testNGConfiguration.getExcludedGroups());
    }

    @Test
    public void testWithOne() throws IOException
    {
        File file = createTestFile(EXCLUDEDLINE.substring(1));
        TestNGConfiguration testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNotNull(testNGConfiguration.getExcludedGroups());
        assertEquals(EXCLUDED, testNGConfiguration.getExcludedGroups());
    }

    @Test
    public void testWithThree() throws IOException
    {
        File file = createTestFile("#" + EXCLUDEDLINE);
        TestNGConfiguration testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNotNull(testNGConfiguration.getExcludedGroups());
        assertEquals(EXCLUDED, testNGConfiguration.getExcludedGroups());
    }

    @Test
    public void testReadingIncludedGroup() throws IOException
    {
        TestNGConfiguration testNGConfiguration = new TestNGConfiguration();
        File file = createTestFile(GROUPSLINE);
        assertNull(testNGConfiguration.getGroups());

        testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNotNull(testNGConfiguration.getGroups());
        assertEquals(GROUPS, testNGConfiguration.getGroups());
    }

    @Test
    public void testReadingIncludedAndExcludedGroups() throws IOException
    {
        TestNGConfiguration testNGConfiguration = new TestNGConfiguration();
        File file = createTestFile(GROUPSLINE, EXCLUDEDLINE);
        assertNull(testNGConfiguration.getGroups());
        assertNull(testNGConfiguration.getExcludedGroups());

        testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNotNull(testNGConfiguration.getGroups());
        assertEquals(GROUPS, testNGConfiguration.getGroups());
        assertEquals(EXCLUDED, testNGConfiguration.getExcludedGroups());
    }

    @Test
    public void testEmptyGroups() throws IOException
    {
        File file = createTestFile("## excluded-groups= ");
        TestNGConfiguration testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNull(testNGConfiguration.getExcludedGroups());
    }

    @Test
    public void testSpacesInGroupsLine() throws IOException
    {
        final String halloGroup = "hallo";
        File file = createTestFile("##excluded-groups = " + halloGroup + " ");
        TestNGConfiguration testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertEquals(halloGroup, testNGConfiguration.getExcludedGroups());
    }

    @Test
    public void testEmptyFile()
    {
        TestNGConfiguration testNGConfiguration = new TestNGConfiguration();
        File file = new File("testng.config");
        assertNull(testNGConfiguration.getExcludedGroups());
        testNGConfiguration = new TestNGConfigurator(file).getConfig();
        assertNull(testNGConfiguration.getExcludedGroups());
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
