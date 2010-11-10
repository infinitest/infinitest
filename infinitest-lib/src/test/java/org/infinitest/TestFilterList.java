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
