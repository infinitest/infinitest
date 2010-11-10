package org.infinitest;

import java.util.List;

public class TestQueueEvent
{
    private final List<String> testQueue;
    private final int initialSize;

    public TestQueueEvent(List<String> testQueue, int initialSize)
    {
        this.testQueue = testQueue;
        this.initialSize = initialSize;
    }

    public List<String> getTestQueue()
    {
        return testQueue;
    }

    public int getInitialSize()
    {
        return initialSize;
    }

    // Now that we can execute tests in parallel, what does this mean?
    public String getCurrentTest()
    {
        return testQueue.get(0);
    }

    public int getTestsRun()
    {
        return getInitialSize() - getTestQueue().size();
    }
}
