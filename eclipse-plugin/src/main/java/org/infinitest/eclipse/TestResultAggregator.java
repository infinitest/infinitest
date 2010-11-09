package org.infinitest.eclipse;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;

import java.util.List;

import org.infinitest.InfinitestCore;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResultsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestResultAggregator implements CoreLifecycleListener, TestResultsListener
{
    private List<TestResultsListener> listeners = newArrayList();

    @Autowired
    public void addListeners(AggregateResultsListener... resultsListeners)
    {
        this.listeners.addAll(asList(resultsListeners));
    }

    public void coreCreated(InfinitestCore core)
    {
        core.addTestResultsListener(this);
    }

    public void coreRemoved(InfinitestCore core)
    {
        core.removeTestResultsListener(this);
    }

    public void testCaseComplete(TestCaseEvent event)
    {
        for (TestResultsListener each : listeners)
            each.testCaseComplete(event);
    }

    public void testCaseStarting(TestEvent event)
    {
        for (TestResultsListener each : listeners)
            each.testCaseStarting(event);
    }
}
