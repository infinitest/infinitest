package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;

import java.util.Comparator;
import java.util.List;

import org.infinitest.ConcurrencyController;
import org.infinitest.ConsoleOutputListener;
import org.infinitest.ReloadListener;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.SingleLockConcurrencyController;
import org.infinitest.TestQueueListener;

abstract class AbstractTestRunner implements TestRunner
{
    private final RunnerEventSupport eventSupport;
    private RuntimeEnvironment environment;
    private ConcurrencyController concurrencyController;
    private Comparator<String> testPriority;

    public AbstractTestRunner()
    {
        eventSupport = new RunnerEventSupport(this);
        testPriority = new NoOpComparator();
    }

    public void setConcurrencyController(ConcurrencyController concurrencyController)
    {
        this.concurrencyController = concurrencyController;
    }

    protected ConcurrencyController getConcurrencySemaphore()
    {
        if (concurrencyController == null)
        {
            concurrencyController = new SingleLockConcurrencyController();
        }
        return concurrencyController;
    }

    protected Comparator<String> getTestPriority()
    {
        return testPriority;
    }

    public void setTestPriority(Comparator<String> testPriority)
    {
        this.testPriority = testPriority;
    }

    public void addTestResultsListener(TestResultsListener statusListener)
    {
        eventSupport.addTestStatusListener(statusListener);
    }

    public void removeTestStatusListener(TestResultsListener statusListener)
    {
        eventSupport.removeTestStatusListener(statusListener);
    }

    public void addConsoleOutputListener(ConsoleOutputListener listener)
    {
        eventSupport.addConsoleOutputListener(listener);
    }

    public void removeConsoleOutputListener(ConsoleOutputListener listener)
    {
        eventSupport.removeConsoleOutputListener(listener);
    }

    public void addTestQueueListener(TestQueueListener listener)
    {
        eventSupport.addTestQueueListener(listener);
    }

    public void removeTestQueueListener(ReloadListener listener)
    {
        eventSupport.removeTestQueueListener(listener);
    }

    protected RunnerEventSupport getEventSupport()
    {
        return eventSupport;
    }

    public void runTest(String testClass)
    {
        runTests(newArrayList(testClass));
    }

    public void runTests(List<String> testNames)
    {
        for (String test : testNames)
        {
            runTest(test);
        }
    }

    public void setRuntimeEnvironment(RuntimeEnvironment environment)
    {
        this.environment = environment;
    }

    protected RuntimeEnvironment getRuntimeEnvironment()
    {
        return environment;
    }

    private static class NoOpComparator implements Comparator<String>
    {
        public int compare(String o1, String o2)
        {
            return 0;
        }
    }
}
