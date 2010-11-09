package org.infinitest.testrunner;

import static com.google.common.collect.Maps.*;
import static java.lang.System.*;

import java.util.Map;

import org.infinitest.TestResultsAdapter;

public class RunStatistics extends TestResultsAdapter
{
    private final Map<String, Long> failureTimestamps;

    public RunStatistics()
    {
        failureTimestamps = newHashMap();
    }

    private void update(TestEvent event)
    {
        failureTimestamps.put(event.getTestName(), currentTimeMillis());
    }

    public long getLastFailureTime(String testName)
    {
        if (!failureTimestamps.containsKey(testName))
            return 0;
        return failureTimestamps.get(testName);
    }

    public void testCaseComplete(TestCaseEvent event)
    {
        for (TestEvent each : event.getFailureEvents())
            update(each);
    }
}
