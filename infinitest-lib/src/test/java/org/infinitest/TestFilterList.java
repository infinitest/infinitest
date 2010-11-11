/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
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

import org.infinitest.filter.RegexFileFilter;
import org.infinitest.filter.TestFilter;
import org.junit.Test;

public class TestFilterList
{
    private final static String TEST_FILTER_LIST_REGEX = "org\\.infinitest\\.TestFilterList";

    @Test
    public void testFilterList() throws IOException
    {
        File file = File.createTempFile("filter", "conf");
        file.deleteOnExit();
        PrintWriter writer = new PrintWriter(file);
        try
        {
            writer.println(TEST_FILTER_LIST_REGEX);
            writer.print("!foo.bar");
            writer.print("#foo.bar");
        }
        finally
        {
            writer.close();
        }
        TestFilter filters = new RegexFileFilter(file);
        assertTrue(filters.match(TestFilterList.class.getName()));
    }

    @Test
    public void shouldIgnoreAllBlankLines()
    {
        RegexFileFilter filter = new RegexFileFilter();
        filter.addFilter("");
        filter.addFilter("");
        assertFalse(filter.match("MyClassName"));
    }

    @Test
    public void testJMockClasses()
    {
        RegexFileFilter filterList = new RegexFileFilter();
        filterList.addFilter("org.jmock.test.acceptance.junit4.testdata.JUnit4TestWithNonPublicBeforeMethod");
        assertTrue(filterList.match("org.jmock.test.acceptance.junit4.testdata.JUnit4TestWithNonPublicBeforeMethod"));
    }

    @Test
    public void testAddFilter() throws IOException
    {
        File file = File.createTempFile("filter", "conf");
        file.deleteOnExit();

        RegexFileFilter filters = new RegexFileFilter(file);
        filters.appendFilter(TEST_FILTER_LIST_REGEX);
        assertTrue(filters.match(TestFilterList.class.getName()));

        filters = new RegexFileFilter(file);
        assertTrue(filters.match(TestFilterList.class.getName()));
    }

    @Test
    public void testFiltering()
    {
        RegexFileFilter filters = new RegexFileFilter();
        filters.addFilter("org\\.infinitest\\..*");
        assertFalse(filters.match(com.fakeco.fakeproduct.TestFakeProduct.class.getName()));
        assertFalse(filters.match(com.fakeco.fakeproduct.FakeProduct.class.getName()));
        assertTrue(filters.match(org.infinitest.changedetect.WhenLookingForClassFiles.class.getName()));
    }

    @Test
    public void testSingleTest()
    {
        RegexFileFilter filters = new RegexFileFilter();
        filters.addFilter(TEST_FILTER_LIST_REGEX);
        assertFalse(filters.match(RegexFileFilter.class.getName()));
        assertTrue("Single test should match regex", filters.match(TestFilterList.class.getName()));
    }

    @Test
    public void testLeadingWildcard()
    {
        RegexFileFilter filters = new RegexFileFilter();
        filters.addFilter(".*TestFilterList");
        assertTrue(filters.match(TestFilterList.class.getName()));
    }

    @Test
    public void testTrailingWildcard()
    {
        RegexFileFilter filters = new RegexFileFilter();
        filters.addFilter("org\\..*");
        assertTrue(filters.match(TestFilterList.class.getName()));
    }
}
