package org.infinitest.testrunner;


public class CrashingTestRunner implements NativeRunner
{
    public TestResults runTest(String testClass)
    {
        throw new RuntimeException("StubErrorTestRunner is Broken");
    }
}