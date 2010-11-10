package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;
import static org.junit.runner.Description.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class WhenConvertingJUnitEventsToInfinitestEvents
{
    private EventTranslator eventTranslator;
    private Result result;
    private Description description;
    private StubClock stubClock;

    @Before
    public void inContext() throws Exception
    {
        stubClock = new StubClock();
        eventTranslator = new EventTranslator(stubClock);
        description = createTestDescription(getClass(), "methodName");
        eventTranslator.testRunStarted(description);
        result = new Result();
    }

    @Test
    public void shouldCollectEventsInTestResults()
    {
        TestResults results = eventTranslator.getTestResults();
        assertTrue(isEmpty(results));
    }

    @Test
    public void shouldCollectMethodStatistics()
    {
        stubClock.time = 10;
        eventTranslator.testStarted(description);
        stubClock.time = 20;
        eventTranslator.testFinished(description);
        TestResults results = eventTranslator.getTestResults();
        MethodStats methodStats = getOnlyElement(results.getMethodStats());
        assertEquals(10, methodStats.startTime);
        assertEquals(20, methodStats.stopTime);
    }

    @Test
    public void shouldCollectFailureEvents() throws Exception
    {
        AssertionError error = new AssertionError();
        result.createListener().testFailure(new Failure(description, error));
        eventTranslator.testRunFinished(result);
        TestEvent expectedEvent = methodFailed("", getClass().getName(), "methodName", error);
        assertEquals(expectedEvent, getOnlyElement(eventTranslator.getTestResults()));
    }

    @Test
    public void shouldCollectErrorEvents() throws Exception
    {
        Exception error = new NullPointerException();
        result.createListener().testFailure(new Failure(description, error));
        eventTranslator.testRunFinished(result);
        TestEvent expectedEvent = methodFailed("", getClass().getName(), "methodName", error);
        assertEquals(expectedEvent, getOnlyElement(eventTranslator.getTestResults()));
    }
}
