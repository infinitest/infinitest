package org.infinitest;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResultsListener;

@SuppressWarnings("all")
public abstract class TestResultsAdapter implements TestResultsListener
{
    public void testCaseStarting(TestEvent event)
    {
    }

    public void testCaseComplete(TestCaseEvent event)
    {
    }
}
