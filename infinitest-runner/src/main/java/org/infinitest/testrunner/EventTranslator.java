package org.infinitest.testrunner;

import static org.infinitest.testrunner.TestEvent.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

class EventTranslator extends RunListener
{
    private final List<TestEvent> eventsCollected;
    private final Map<Description, MethodStats> methodStats;
    private final Clock clock;

    EventTranslator(Clock clock)
    {
        this.clock = clock;
        eventsCollected = new ArrayList<TestEvent>();
        methodStats = new HashMap<Description, MethodStats>();
    }

    EventTranslator()
    {
        this(new SystemClock());
    }

    @Override
    public void testStarted(Description description)
    {
        getMethodStats(description).startTime = clock.currentTimeMillis();
    }

    @Override
    public void testFinished(Description description)
    {
        getMethodStats(description).stopTime = clock.currentTimeMillis();
    }

    @Override
    public void testRunFinished(Result result)
    {
        for (Failure failure : result.getFailures())
        {
            eventsCollected.add(createEventFrom(failure));
        }
    }

    public TestResults getTestResults()
    {
        TestResults results = new TestResults(eventsCollected);
        results.addMethodStats(methodStats.values());
        return results;
    }

    private TestEvent createEventFrom(Failure failure)
    {
        Description testDescription = failure.getDescription();
        String testCaseName = getTestCaseName(testDescription);
        Throwable exception = failure.getException();
        TestEvent event = methodFailed(failure.getMessage(), testCaseName, getMethodName(testDescription), exception);
        return event;
    }

    private static String getTestCaseName(Description description)
    {
        return description.getDisplayName().split("\\(|\\)")[1];
    }

    private String getMethodName(Description description)
    {
        return description.getDisplayName().split("\\(|\\)")[0];
    }

    private MethodStats getMethodStats(Description description)
    {
        MethodStats stats = methodStats.get(description);
        if (stats == null)
        {
            stats = new MethodStats(getMethodName(description));
            methodStats.put(description, stats);
        }
        return stats;
    }
}
