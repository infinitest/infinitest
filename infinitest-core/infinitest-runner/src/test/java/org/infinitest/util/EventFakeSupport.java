package org.infinitest.util;

import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import junit.framework.AssertionFailedError;

import org.infinitest.testrunner.TestEvent;

public class EventFakeSupport
{
    public static final String FAILURE_MSG = "Test failed as expected";
    public static final String TEST_NAME = "com.fakeco.TestFoo";
    public static final String TEST_METHOD = "someMethod";

    public static TestEvent withFailingMethod(String methodName)
    {
        return createEvent(methodName, FAILURE_MSG);
    }

    public static TestEvent withTestNamed(String testName)
    {
        return testCaseStarting(testName);
    }

    public static TestEvent createEvent(String methodName, Throwable error)
    {
        return new TestEvent(METHOD_FAILURE, error.getMessage(), TEST_NAME, methodName, error);
    }

    public static TestEvent createEvent(String methodName, String msg)
    {
        String testMsg = "testFailed(" + msg + ")";
        return new TestEvent(METHOD_FAILURE, testMsg, TEST_NAME, methodName, new AssertionFailedError());
    }

    public static TestEvent eventWithError(Throwable error)
    {
        return new TestEvent(METHOD_FAILURE, error.getMessage(), TEST_NAME, TEST_METHOD, error);
    }

    public static TestEvent withATest()
    {
        return new TestEvent(TEST_CASE_STARTING, "", TEST_NAME, TEST_METHOD, null);
    }

    public static TestEvent withTest(Class<?> testClass)
    {
        return withTestNamed(testClass.getName());
    }
}
