package org.infinitest.testrunner.queue;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class TestQueue extends PriorityBlockingQueue<String>
{
    private static final long serialVersionUID = -1L;

    public TestQueue(Comparator<String> comparator)
    {
        super(11, comparator);
    }

    @Override
    public boolean add(String testName)
    {
        if (!contains(testName))
        {
            return super.add(testName);
        }
        return false;
    }
}
