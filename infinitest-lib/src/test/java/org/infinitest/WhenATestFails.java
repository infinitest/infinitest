package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

public class WhenATestFails extends ResultCollectorTestSupport
{
    private DefaultInfinitestCore core;
    private ControlledEventQueue eventQueue;
    private TestCaseEvent testCaseEvent;

    @Before
    public void inContext()
    {
        eventQueue = new ControlledEventQueue();
        core = createCore(withChangedFiles(), withTests(FAILING_TEST), eventQueue);
        collector = new ResultCollector(core);
    }

    @Test
    public void shouldChangeStatusToFailing()
    {
        testRun(methodFailed("", "", "", null));
        collector.testQueueUpdated(new TestQueueEvent(emptyStringList(), 1));
        collector.testRunComplete();
        assertEquals(FAILING, collector.getStatus());
    }

    @Test
    public void shouldBeAbleToLocateTheSourceOfAnEvent()
    {
        core.addTestResultsListener(new TestResultsAdapter()
        {
            @Override
            public void testCaseComplete(TestCaseEvent event)
            {
                testCaseEvent = event;
            }
        });
        core.update();
        eventQueue.flush();

        assertTrue(core.isEventSourceFor(testCaseEvent));
        TestResults resultCopy = new TestResults(testCaseEvent.getFailureEvents());
        TestCaseEvent altEvent = new TestCaseEvent(testCaseEvent.getTestName(), this, resultCopy);
        assertFalse(core.isEventSourceFor(altEvent));
    }

    @Test
    public void shouldFireFailureEvents()
    {
        EventSupport eventSupport = new EventSupport();
        core.addTestResultsListener(eventSupport);
        core.update();
        eventQueue.flush();
        eventSupport.assertTestsStarted(FAILING_TEST);
        eventSupport.assertTestFailed(FAILING_TEST);
        assertTrue(collector.hasFailures());
    }

    @Test
    public void shouldRemoveAllFailuresOnReload()
    {
        shouldChangeStatusToFailing();
        collector.reloading();
        assertEquals(SCANNING, collector.getStatus());
        assertTrue(collector.getFailures().isEmpty());
        assertEquals(0, collector.getPointOfFailureCount());
        assertTrue(collector.getPointsOfFailure().isEmpty());
    }
}
