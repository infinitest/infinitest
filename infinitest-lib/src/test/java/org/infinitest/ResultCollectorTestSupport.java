package org.infinitest;

import static java.util.Arrays.*;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;

public class ResultCollectorTestSupport
{
    protected static final String DEFAULT_TEST_NAME = "testName";
    protected ResultCollector collector;
    protected FailureListenerSupport listener;

    @Before
    public final void processingEventsFromCore()
    {
        collector = new ResultCollector();
        listener = new FailureListenerSupport();
        collector.addChangeListener(listener);
    }

    protected void testRun(TestEvent... events)
    {
        String testName = DEFAULT_TEST_NAME;
        if (events.length != 0)
            testName = events[0].getTestName();
        testRunWith(testName, events);
    }

    protected void testRunWith(String testName, TestEvent... events)
    {
        collector.testCaseComplete(new TestCaseEvent(testName, this, new TestResults(asList(events))));
    }
}
