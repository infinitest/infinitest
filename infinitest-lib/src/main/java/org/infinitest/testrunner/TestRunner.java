package org.infinitest.testrunner;

import java.util.Comparator;
import java.util.List;

import org.infinitest.ConcurrencyController;
import org.infinitest.ConsoleOutputListener;
import org.infinitest.ReloadListener;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.TestQueueListener;

public interface TestRunner
{
    void setTestPriority(Comparator<String> testPriority);

    void runTests(List<String> testNames);

    void addTestResultsListener(TestResultsListener listener);

    void removeTestStatusListener(TestResultsListener listener);

    void setRuntimeEnvironment(RuntimeEnvironment environment);

    void addConsoleOutputListener(ConsoleOutputListener listener);

    void removeConsoleOutputListener(ConsoleOutputListener listener);

    void addTestQueueListener(TestQueueListener listener);

    void removeTestQueueListener(ReloadListener testQueueNormalizer);

    void setConcurrencyController(ConcurrencyController semaphore);
}
