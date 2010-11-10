package org.infinitest.testrunner;

import static org.infinitest.testrunner.TestEvent.*;

public class FakeRunner implements NativeRunner
{
    public TestResults runTest(String testClass)
    {
        return new TestResults(methodFailed(testClass, "methodName", new Throwable()));
    }
}
