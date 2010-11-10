package org.infinitest;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static java.util.Arrays.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestEvent.TestState;
import org.infinitest.testrunner.TestResults;

public class EventSupport extends TestResultsAdapter implements StatusChangeListener, TestQueueListener
{
    private final List<TestEvent> testEvents;
    private final Map<String, TestCaseEvent> testCaseEvents;
    private final List<PropertyChangeEvent> propertyEvents;
    private ThreadSafeFlag runComplete;
    private final List<TestQueueEvent> queueEvents;
    private final long timeout;
    private final ThreadSafeFlag reload;
    private int reloadCount;

    public EventSupport(long timeout)
    {
        testCaseEvents = newHashMap();
        testEvents = newArrayList();
        propertyEvents = newArrayList();
        queueEvents = newArrayList();
        runComplete = new ThreadSafeFlag(timeout);
        reload = new ThreadSafeFlag(timeout);
        this.timeout = timeout;
    }

    public EventSupport()
    {
        this(5000);
    }

    @Override
    public void testCaseStarting(TestEvent event)
    {
        testEvents.add(event);
    }

    public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        propertyEvents.add(new PropertyChangeEvent(this, "doesn't matter", oldStatus, newStatus));
    }

    public void testRunComplete()
    {
        runComplete.trip();
    }

    public void testQueueUpdated(TestQueueEvent event)
    {
        synchronized (queueEvents)
        {
            queueEvents.add(event);
            queueEvents.notify();
        }
    }

    public void reloading()
    {
        reload.trip();
        reloadCount++;
    }

    public void assertEventsReceived(TestState... expectedTypes)
    {
        ArrayList<TestState> actualTypes = new ArrayList<TestState>();
        for (TestEvent event : testEvents)
        {
            actualTypes.add(event.getType());
        }
        assertEquals(asList(expectedTypes), actualTypes);
    }

    public void reset()
    {
        resetEvents();
    }

    public void resetEvents()
    {
        testEvents.clear();
        testCaseEvents.clear();
    }

    public void assertStatesChanged(CoreStatus... expectedStates)
    {
        ArrayList<CoreStatus> actualStates = new ArrayList<CoreStatus>();
        for (PropertyChangeEvent event : propertyEvents)
        {
            actualStates.add((CoreStatus) event.getNewValue());
        }
        assertEquals(asList(expectedStates), actualStates);
    }

    public void assertTestRun(Class<?> clazz)
    {
        assertTestRun(clazz.getName());
    }

    public void assertTestRun(String name)
    {
        TestCaseEvent event = testCaseEvents.get(name);
        assertNotNull(name + " was never run!", event);
    }

    public void assertTestPassed(String name)
    {
        assertTestRun(name);
        assertFalse(name + " failed! Expected to pass", testCaseEvents.get(name).failed());
    }

    public void assertTestFailed(String name)
    {
        assertTestRun(name);
        assertTrue(name + " passed! Expected to fail", testCaseEvents.get(name).failed());
    }

    public void assertRunComplete() throws InterruptedException
    {
        runComplete.assertTripped();
        resetRunComplete();
    }

    public void assertQueueChanges(int expectedEventCount) throws InterruptedException
    {
        synchronized (queueEvents)
        {
            while (queueEvents.size() < expectedEventCount)
            {
                queueEvents.wait(timeout);
            }
        }
    }

    public void assertReloadOccured() throws InterruptedException
    {
        reload.assertTripped();
    }

    public int getReloadCount()
    {
        return reloadCount;
    }

    public void assertTestPassed(Class<?> clazz)
    {
        assertTestPassed(clazz.getName());
    }

    public void assertTestFailed(Class<?> clazz)
    {
        assertTestFailed(clazz.getName());
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (TestEvent each : testEvents)
        {
            builder.append(each.toString() + each.getErrorClassName() + each.getMessage());
        }
        return builder.toString();
    }

    public Iterable<String> getTestsRun()
    {
        Set<String> tests = newHashSet();
        for (TestEvent event : testEvents)
        {
            tests.add(event.getTestName());
        }
        return tests;
    }

    private void resetRunComplete()
    {
        runComplete = new ThreadSafeFlag(timeout);
    }

    @Override
    public void testCaseComplete(TestCaseEvent event)
    {
        for (TestEvent each : event.getFailureEvents())
        {
            testEvents.add(each);
        }

        testCaseEvents.put(event.getTestName(), event);
    }

    public static TestCaseEvent testCaseFailing(String testName, String methodName, Throwable throwable)
    {
        return new TestCaseEvent(testName, new Object(), new TestResults(methodFailed(testName, methodName, throwable)));
    }

    public void assertTestsStarted(String... testNames)
    {
        // Also asserts the order
        List<String> startedTests = new ArrayList<String>();
        for (TestEvent event : testEvents)
        {
            if (event.getType().equals(TEST_CASE_STARTING))
            {
                startedTests.add(event.getTestName());
            }
        }
        assertEquals(asList(testNames), startedTests);
    }

    public void assertTestsStarted(Class<?>... classes)
    {
        List<String> testNames = newArrayList();
        for (Class<?> each : classes)
        {
            testNames.add(each.getName());
        }
        assertTestsStarted(newArray(testNames, String.class));
    }
}
