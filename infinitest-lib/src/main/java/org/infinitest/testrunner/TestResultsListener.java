package org.infinitest.testrunner;

public interface TestResultsListener
{
    void testCaseStarting(TestEvent event);

    void testCaseComplete(TestCaseEvent event);
}
