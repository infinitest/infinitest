package org.infinitest.testrunner.process;

import org.infinitest.testrunner.TestResults;

public interface ProcessConnection
{
    TestResults runTest(String testName);

    void close();

    boolean abort();
}