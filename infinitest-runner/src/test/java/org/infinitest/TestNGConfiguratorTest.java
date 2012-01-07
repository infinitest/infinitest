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

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;

public class TestNGConfiguratorTest
{
    private static final String LISTENER2 = "org.testng.reporters.JUnitXMLReporter";
    private static final String LISTENER1 = "org.testng.internal.annotations.DefaultAnnotationTransformer";
    private static final String GROUPS = "quick";
    private static final String EXCLUDED = "slow, broken, manual";
    private static final Object LISTENERS = LISTENER1 + ", " + LISTENER2;
    private static final String EXCLUDEDLINE = "## excluded-groups=" + EXCLUDED;
    private static final String GROUPSLINE = "## groups=" + GROUPS;
    private static final String LISTENERLINE = "## listeners=" + LISTENERS;

    @Test
    public void canExcludeNothing()
    {
        TestNGConfiguration config = new TestNGConfiguration();

        assertThat(config.getExcludedGroups()).isNull();
    }

    @Test
    public void canIncludeNothing()
    {
        TestNGConfiguration config = new TestNGConfiguration();

        assertThat(config.getGroups()).isNull();
    }

    @Test
    public void canFilterFromFile() throws IOException
    {
        File file = file(EXCLUDEDLINE);

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
    }

    @Test
    public void testWithOne() throws IOException
    {
        File file = file(EXCLUDEDLINE.substring(1));

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
    }

    @Test
    public void testWithThree() throws IOException
    {
        File file = file("#" + EXCLUDEDLINE);

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
    }

    @Test
    public void testReadingIncludedGroup() throws IOException
    {
        File file = file(GROUPSLINE);

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getGroups()).isEqualTo(GROUPS);
    }

    @Test
    public void testReadingIncludedAndExcludedGroups() throws IOException
    {
        File file = file(GROUPSLINE, EXCLUDEDLINE);

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getGroups()).isEqualTo(GROUPS);
        assertThat(config.getExcludedGroups()).isEqualTo(EXCLUDED);
    }

    @Test
    public void testEmptyGroups() throws IOException
    {
        File file = file("## excluded-groups= ");

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getExcludedGroups()).isNull();
    }

    @Test
    public void testSpacesInGroupsLine() throws IOException
    {
        String halloGroup = "hallo";
        File file = file("##excluded-groups = " + halloGroup + " ");

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getExcludedGroups()).isEqualTo(halloGroup);
    }

    @Test
    public void testEmptyFile()
    {
        File file = new File("testng.config");

        TestNGConfiguration config = new TestNGConfigurator(file).getConfig();

        assertThat(config.getExcludedGroups()).isNull();
    }

    @Test
    public void testReadingListeners() throws IOException
    {
        File file = file(LISTENERLINE);

        List<?> listeners = new TestNGConfigurator(file).getConfig().getListeners();
        String listenerName1 = listeners.get(0).getClass().getName();
        String listenerName2 = listeners.get(1).getClass().getName();

        assertTrue(listenerName1.equals(LISTENER1) || listenerName1.equals(LISTENER2));
        assertTrue(listenerName2.equals(LISTENER1) || listenerName2.equals(LISTENER2));
    }

    private static File file(String... additionalLines) throws IOException
    {
        File file = File.createTempFile("filter", "conf");
        file.deleteOnExit();
        PrintWriter writer = new PrintWriter(file);
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
