package org.infinitest.testrunner;

import static java.util.Arrays.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TestResults implements Iterable<TestEvent>, Serializable
{
    private final List<TestEvent> eventsCollected;
    private List<MethodStats> methodStats = new LinkedList<MethodStats>();

    public TestResults(List<TestEvent> eventsCollected)
    {
        this.eventsCollected = eventsCollected;
    }

    public TestResults(TestEvent... failures)
    {
        this(asList(failures));
    }

    public Iterator<TestEvent> iterator()
    {
        return eventsCollected.iterator();
    }

    public Iterable<MethodStats> getMethodStats()
    {
        return methodStats;
    }

    public void addMethodStats(Collection<MethodStats> methodStatistics)
    {
        methodStats.addAll(methodStatistics);
    }
}
