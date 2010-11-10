package org.infinitest.intellij.plugin.launcher;

import org.infinitest.CoreStatus;

public class StatusMessages
{
    public static String getMessage(CoreStatus status)
    {
        switch (status)
        {
        case SCANNING:
            return "Watching for Changes";
        case FAILING:
            return "Ran $TEST_COUNT Tests - Failures Detected";
        case INDEXING:
            return "Building Class Index";
        case PASSING:
            return "Ran $TEST_COUNT Tests - All Pass";
        case RUNNING:
            return "Running $TESTS_RAN of $TEST_COUNT - $CURRENT_TEST";
        default:
            throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
