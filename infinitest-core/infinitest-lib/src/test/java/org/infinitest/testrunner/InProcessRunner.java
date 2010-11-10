package org.infinitest.testrunner;

import java.util.List;

public class InProcessRunner extends AbstractTestRunner
{
    private NativeRunner runner;

    public InProcessRunner()
    {
        runner = new JUnit4Runner();
    }

    @Override
    public void runTests(List<String> testClasses)
    {
        for (String testClass : testClasses)
        {
            getEventSupport().fireStartingEvent(testClass);
            getEventSupport().fireTestCaseComplete(testClass, runner.runTest(testClass));
        }
        getEventSupport().fireTestRunComplete();
    }
}
