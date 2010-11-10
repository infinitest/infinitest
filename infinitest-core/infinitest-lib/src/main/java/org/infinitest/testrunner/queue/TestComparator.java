package org.infinitest.testrunner.queue;

import java.util.Comparator;

import org.infinitest.testrunner.RunStatistics;

public class TestComparator implements Comparator<String>
{
    private final RunStatistics stats;

    public TestComparator(RunStatistics stats)
    {
        this.stats = stats;
    }

    public int compare(String test1, String test2)
    {
        return new Long(stats.getLastFailureTime(test2)).compareTo(stats.getLastFailureTime(test1));
    }
}
