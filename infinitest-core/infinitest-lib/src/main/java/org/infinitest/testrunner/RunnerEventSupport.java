package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static org.infinitest.testrunner.TestEvent.*;

import java.util.List;

import org.infinitest.ConsoleOutputListener;
import org.infinitest.ReloadListener;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestQueueListener;
import org.infinitest.ConsoleOutputListener.OutputType;

public class RunnerEventSupport
{
    private List<ConsoleOutputListener> consoleListenerList;
    private List<TestQueueListener> testQueueListenerList;
    private List<TestResultsListener> listeners;
    private Object source;

    public RunnerEventSupport(Object eventSource)
    {
        source = eventSource;
        listeners = newArrayList();
        consoleListenerList = newArrayList();
        testQueueListenerList = newArrayList();
    }

    public void addTestStatusListener(TestResultsListener listener)
    {
        listeners.add(listener);
    }

    public void removeTestStatusListener(TestResultsListener listener)
    {
        listeners.remove(listener);
    }

    private void fireTestEvent(TestEvent testEvent)
    {
        for (TestResultsListener each : listeners)
        {
            switch (testEvent.getType())
            {
            case TEST_CASE_STARTING:
                each.testCaseStarting(testEvent);
                break;
            default:
                throw new IllegalArgumentException("Unknown event type:" + testEvent);
            }
        }
    }

    public void fireTestCaseComplete(String testName, TestResults results)
    {
        for (TestResultsListener each : listeners)
            each.testCaseComplete(new TestCaseEvent(testName, source, results));
    }

    public void fireStartingEvent(String testClass)
    {
        fireTestEvent(testCaseStarting(testClass));
    }

    public void addConsoleOutputListener(ConsoleOutputListener listener)
    {
        consoleListenerList.add(listener);
    }

    public void removeConsoleOutputListener(ConsoleOutputListener listener)
    {
        consoleListenerList.remove(listener);
    }

    public void fireTestRunComplete()
    {
        for (TestQueueListener each : testQueueListenerList)
            each.testRunComplete();
    }

    public void fireConsoleUpdateEvent(String newText, OutputType outputType)
    {
        for (ConsoleOutputListener each : consoleListenerList)
            each.consoleOutputUpdate(newText, outputType);
    }

    public void addTestQueueListener(TestQueueListener listener)
    {
        testQueueListenerList.add(listener);
    }

    public void removeTestQueueListener(ReloadListener listener)
    {
        testQueueListenerList.remove(listener);
    }

    public void fireQueueEvent(TestQueueEvent event)
    {
        for (TestQueueListener each : testQueueListenerList)
            each.testQueueUpdated(event);
    }

}
