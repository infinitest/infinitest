package org.infinitest.testrunner;


public class RunnerThatCannotBeCreated implements NativeRunner
{
    public RunnerThatCannotBeCreated()
    {
        throw new RuntimeException("This runner cannot be created");
    }

    public TestResults runTest(String testClass)
    {
        throw new UnsupportedOperationException();
    }
}
