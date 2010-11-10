package org.infinitest.testrunner.queue;

import static java.lang.Thread.*;
import static java.util.Arrays.*;
import static org.infinitest.EventSupport.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.RunStatistics;
import org.junit.Before;
import org.junit.Test;

public class WhenPrioritizingTests
{
    private RunStatistics stats;
    private TestQueue queue;

    @Before
    public void inContext()
    {
        stats = new RunStatistics();
        queue = new TestQueue(new TestComparator(stats));
    }

    @Test
    public void shouldPreferRecentlyFailedTests() throws Exception
    {
        stats.testCaseComplete(testCaseFailing("test2", "", new Exception()));
        sleep(2);
        stats.testCaseComplete(testCaseFailing("test1", "", new Exception()));

        queue.add("test1");
        queue.add("test2");
        assertEquals("test1", queue.take());
        assertEquals("test2", queue.take());
    }

    @Test
    public void shouldNeverRunTheSameTestTwice()
    {
        queue.addAll(asList("test1", "test2", "test1"));
        assertEquals(2, queue.size());
    }
}
