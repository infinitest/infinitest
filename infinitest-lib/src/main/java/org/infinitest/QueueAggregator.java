package org.infinitest;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static java.lang.Math.*;

import java.util.List;
import java.util.Map;

public class QueueAggregator
{
    private int initialSize = 0;
    private Map<InfinitestCore, AggregatingQueueListener> coreQueueListeners;
    public static final String STATUS_PROPERTY = "status";
    private List<TestQueueListener> queueListeners;

    public QueueAggregator()
    {
        queueListeners = newArrayList();
        coreQueueListeners = newLinkedHashMap();
    }

    public void addListener(TestQueueListener testQueueAdapter)
    {
        queueListeners.add(testQueueAdapter);
    }

    public void removeTestQueueListener(TestQueueListener listener)
    {
        queueListeners.remove(listener);
    }

    public void attach(InfinitestCore core)
    {
        AggregatingQueueListener listener = new AggregatingQueueListener();
        coreQueueListeners.put(core, listener);
        core.addTestQueueListener(listener);
    }

    public void detach(InfinitestCore core)
    {
        core.removeTestQueueListener(coreQueueListeners.remove(core));
    }

    private void fireTestQueueEvent()
    {
        List<String> aggregatedQueue = getAggregatedQueue();
        initialSize = max(initialSize, aggregatedQueue.size());
        for (TestQueueListener each : queueListeners)
            each.testQueueUpdated(new TestQueueEvent(aggregatedQueue, initialSize));
        if (aggregatedQueue.isEmpty())
            initialSize = 0;
    }

    private List<String> getAggregatedQueue()
    {
        List<String> aggregatedQueue = newArrayList();
        for (AggregatingQueueListener each : coreQueueListeners.values())
            aggregatedQueue.addAll(each.currentQueue);
        return aggregatedQueue;
    }

    private void fireReloadingEvent()
    {
        for (TestQueueListener each : queueListeners)
            each.reloading();
    }

    private void fireCompleteEvent()
    {
        if (getAggregatedQueue().isEmpty())
            for (TestQueueListener each : queueListeners)
                each.testRunComplete();
    }

    Map<InfinitestCore, AggregatingQueueListener> getCoreQueueListeners()
    {
        return coreQueueListeners;
    }

    private class AggregatingQueueListener extends TestQueueAdapter
    {
        private List<String> currentQueue = newArrayList();

        @Override
        public void reloading()
        {
            fireReloadingEvent();
        }

        @Override
        public void testRunComplete()
        {
            fireCompleteEvent();
        }

        @Override
        public void testQueueUpdated(TestQueueEvent event)
        {
            currentQueue = event.getTestQueue();
            fireTestQueueEvent();
        }
    }

}
