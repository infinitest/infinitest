package org.infinitest.testrunner;

public class HangingRunner implements NativeRunner
{
    public TestResults runTest(String testClass)
    {
        while (true)
        {
            // hang out
        }
    }
}
