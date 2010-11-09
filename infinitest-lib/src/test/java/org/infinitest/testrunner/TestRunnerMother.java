package org.infinitest.testrunner;

import static org.infinitest.util.FakeEnvironments.*;

import org.infinitest.testrunner.process.NativeConnectionFactory;

public abstract class TestRunnerMother
{
    public static AbstractTestRunner createRunner()
    {
        return new MultiProcessRunner(new NativeConnectionFactory(JUnit4Runner.class), fakeEnvironment());
    }
}
