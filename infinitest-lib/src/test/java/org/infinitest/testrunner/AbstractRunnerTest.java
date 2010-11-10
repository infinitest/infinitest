package org.infinitest.testrunner;

import java.util.LinkedList;
import java.util.List;

import org.infinitest.TestQueueAdapter;
import org.infinitest.ThreadSafeFlag;
import org.junit.Test;

import com.fakeco.fakeproduct.TestJUnit4TestCase;

public abstract class AbstractRunnerTest
{
    private ThreadSafeFlag runComplete;

    @Test
    public void shouldFireEventWhenTestRunIsComplete() throws Exception
    {
        runComplete = new ThreadSafeFlag();
        getRunner().addTestQueueListener(new TestQueueAdapter()
        {
            @Override
            public void testRunComplete()
            {
                runComplete.trip();
            }
        });
        getRunner().runTest(TestJUnit4TestCase.class.getName());
        runComplete.assertTripped();
    }

    protected void runTest(String testName) throws InterruptedException
    {
        getRunner().runTest(testName);
        waitForCompletion();
    }

    protected void runTests(Class<?>... tests) throws InterruptedException
    {
        List<String> testNames = new LinkedList<String>();
        for (Class<?> each : tests)
            testNames.add(each.getName());
        getRunner().runTests(testNames);
        waitForCompletion();
    }

    protected abstract void waitForCompletion() throws InterruptedException;

    protected abstract AbstractTestRunner getRunner();

}