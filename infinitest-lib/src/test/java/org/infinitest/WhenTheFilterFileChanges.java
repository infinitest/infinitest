package org.infinitest;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;

import org.infinitest.filter.RegexFileFilter;
import org.infinitest.filter.TestFilter;
import org.infinitest.parser.ClassFileTestDetector;
import org.infinitest.parser.TestDetector;
import org.junit.Test;

public class WhenTheFilterFileChanges
{
    private TestFilter list;
    private final String CLASS_NAME = "com.foo.Bar";

    @Test
    public void shouldUpdateTheFilterList() throws Exception
    {
        File file = File.createTempFile("infinitest", "shouldUpdateTheFilterList");
        list = new RegexFileFilter(file);
        assertFalse(list.match(CLASS_NAME));

        PrintWriter writer = new PrintWriter(file);
        writer.println(CLASS_NAME);
        writer.close();
        list.updateFilterList();
        assertTrue(list.match(CLASS_NAME));
    }

    @Test
    public void shouldRecognizeChangesBeforeLookingForTests()
    {
        TestFilter fakeFilterList = new RegexFileFilter()
        {
            @Override
            public void updateFilterList()
            {
                addFilter(CLASS_NAME);
            }
        };
        TestDetector detector = new ClassFileTestDetector(fakeFilterList);
        detector.setClasspathProvider(emptyClasspath());
        detector.findTestsToRun(Collections.<File> emptySet());
        assertTrue(fakeFilterList.match(CLASS_NAME));
    }
}
