package org.infinitest;

import java.io.File;
import java.util.Collection;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResultsListener;

@SuppressWarnings("all")
public class FakeInfinitestCore implements InfinitestCore
{
    public void addTestQueueListener(TestQueueListener listener)
    {
    }

    public void removeTestQueueListener(TestQueueListener listener)
    {
    }

    public void addTestResultsListener(TestResultsListener listener)
    {
    }

    public void removeTestResultsListener(TestResultsListener listener)
    {
    }

    public int update()
    {
        return 0;
    }

    public void reload()
    {
    }

    public void setRuntimeEnvironment(RuntimeEnvironment environment)
    {
    }

    public String getName()
    {
        return null;
    }

    public void addConsoleOutputListener(ConsoleOutputListener listener)
    {
    }

    public void removeConsoleOutputListener(ConsoleOutputListener listener)
    {
    }

    public void addReloadListener(ReloadListener listener)
    {
    }

    public void addDisabledTestListener(DisabledTestListener listener)
    {
    }

    public void removeDisabledTestListener(DisabledTestListener anyObject)
    {
        throw new UnsupportedOperationException();
    }

    public RuntimeEnvironment getRuntimeEnvironment()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isEventSourceFor(TestCaseEvent testCaseEvent)
    {
        throw new UnsupportedOperationException();
    }

    public int update(Collection<File> changedFiles)
    {
        throw new UnsupportedOperationException();
    }
}
