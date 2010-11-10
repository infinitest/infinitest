package org.infinitest;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import java.util.List;

import junit.framework.AssertionFailedError;

import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;
import org.junit.Test;

import com.google.common.collect.Iterables;

public class WhenTrackingTestResults extends ResultCollectorTestSupport
{
    @Test
    public void shouldOrderMostRecentFailuresFirst()
    {
        TestEvent mostRecentFailure = eventWithError(new NullPointerException());

        testRun(eventWithError(new AssertionFailedError()));
        testRun(mostRecentFailure);

        assertEquals(mostRecentFailure.getPointOfFailure(), collector.getPointOfFailure(0));
    }

    @Test
    public void shouldUnifyEventsWithSamePointOfFailure()
    {
        Throwable pointOfFailure = new AssertionFailedError().fillInStackTrace();

        testRun(createEvent("method1", pointOfFailure), createEvent("method2", pointOfFailure),
                        createEvent("method3", new AssertionError()));

        assertEquals(2, collector.getPointOfFailureCount());
        assertEquals(3, collector.getFailures().size());
    }

    @Test
    public void shouldFireEventsToNotifyListenerWhenTestCaseIsComplete()
    {
        assertTrue(listener.failures.isEmpty());

        TestEvent failure = eventWithError(new AssertionFailedError());
        testRun(failure);
        assertEquals(failure, getOnlyElement(listener.failures));
    }

    @Test
    public void shouldClearResultsWhenStatusChangesToReloading()
    {
        TestEvent event = eventWithError(new AssertionFailedError());
        testRun(event);
        collector.reloading();

        assertEquals(0, collector.getPointOfFailureCount());
        assertFalse(collector.hasFailures());
        assertEquals(event, Iterables.getOnlyElement(listener.removed));
    }

    @Test
    public void shouldIndicateFailures()
    {
        testRun(eventWithError(new AssertionFailedError()));

        assertTrue(collector.hasFailures());
    }

    @Test
    public void shouldFireUpdateEventsWhenFailuresChange()
    {
        TestEvent event = eventWithError(new AssertionFailedError());
        testRun(event);
        assertTrue(listener.removed.isEmpty());
        assertTrue(listener.changed.isEmpty());

        listener.clear();
        event = eventWithError(new AssertionFailedError("Different message"));
        testRun(event);

        assertSame(event, getOnlyElement(listener.changed));
        assertTrue(listener.removed.isEmpty());
        assertTrue(listener.added.toString(), listener.added.isEmpty());
    }

    @Test
    public void shouldProvideUniqueSetOfPointsOfFailure()
    {
        Exception firstException = new Exception("Some message.");
        Exception secondException = new Exception("Some other message.");
        testRun(methodFailed("shouldFoo", "", firstException), methodFailed("shouldBar", "", firstException),
                        methodFailed("shouldBaz", "", secondException));

        assertEquals(2, collector.getPointsOfFailure().size());
    }

    @Test
    public void shouldProvideAllFailuresForSpecificPointOfFailure()
    {
        Exception firstException = new Exception("Some message.");
        Exception secondException = new Exception("Some other message.");
        testRun(methodFailed("shouldFoo", "", firstException), methodFailed("shouldBar", "", firstException),
                        methodFailed("shouldBaz", "", secondException));

        for (PointOfFailure pointOfFailure : collector.getPointsOfFailure())
        {
            if (pointOfFailure.getMessage().contains("other message"))
            {
                assertEquals(1, collector.getFailuresForPointOfFailure(pointOfFailure).size());
            }
            else
            {
                assertEquals(2, collector.getFailuresForPointOfFailure(pointOfFailure).size());
            }
        }
    }

    @Test
    public void canClearAllData()
    {
        testRun(withFailingMethod("shouldFoo"));
        List<TestEvent> failures = collector.getFailures();

        assertEquals(1, failures.size());
        PointOfFailure pointOfFailure = getOnlyElement(failures).getPointOfFailure();
        assertEquals(1, collector.getFailuresForPointOfFailure(pointOfFailure).size());

        collector.clear();
        failures = collector.getFailures();
        assertEquals(0, failures.size());
        assertEquals(0, collector.getFailuresForPointOfFailure(pointOfFailure).size());
    }
}
