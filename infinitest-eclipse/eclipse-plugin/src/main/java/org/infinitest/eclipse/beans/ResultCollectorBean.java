package org.infinitest.eclipse.beans;

import org.infinitest.FailureListListener;
import org.infinitest.ResultCollector;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestQueueListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultCollectorBean extends ResultCollector
{
    // Too simple to break, no?

    @Autowired
    public void addChangeListeners(FailureListListener... listeners)
    {
        for (FailureListListener each : listeners)
            addChangeListener(each);
    }

    @Autowired
    public void addTestQueueListeners(TestQueueListener... listeners)
    {
        for (TestQueueListener each : listeners)
            addTestQueueListener(each);
    }

    @Autowired
    public void addStatusChangeListeners(StatusChangeListener... listeners)
    {
        for (StatusChangeListener each : listeners)
            addStatusChangeListener(each);
    }
}
