package org.infinitest.testrunner;

import static org.infinitest.testrunner.TestEvent.*;

public class FailingRunner implements NativeRunner
{
    public static final TestEvent FAILING_EVENT = methodFailed("testName", "methodName", new AssertionError());

    public TestResults runTest(String testClass)
    {
        return new TestResults(FAILING_EVENT);
    }
}
