/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static org.infinitest.testrunner.TestEvent.*;

import java.util.List;

import org.infinitest.ConsoleOutputListener;
import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.ReloadListener;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestQueueListener;

public class RunnerEventSupport
{
    private final List<ConsoleOutputListener> consoleListenerList;
    private final List<TestQueueListener> testQueueListenerList;
    private final List<TestResultsListener> listeners;
    private final Object source;

    public RunnerEventSupport(Object eventSource)
    {
        source = eventSource;
        listeners = newArrayList();
        consoleListenerList = newArrayList();
        testQueueListenerList = newArrayList();
    }

    public void addTestStatusListener(TestResultsListener listener)
    {
        listeners.add(listener);
    }

    public void removeTestStatusListener(TestResultsListener listener)
    {
        listeners.remove(listener);
    }

    private void fireTestEvent(TestEvent testEvent)
    {
        for (TestResultsListener each : listeners)
        {
            switch (testEvent.getType())
            {
            case TEST_CASE_STARTING:
                each.testCaseStarting(testEvent);
                break;
            default:
                throw new IllegalArgumentException("Unknown event type:" + testEvent);
            }
        }
    }

    public void fireTestCaseComplete(String testName, TestResults results)
    {
        for (TestResultsListener each : listeners)
        {
            each.testCaseComplete(new TestCaseEvent(testName, source, results));
        }
    }

    public void fireStartingEvent(String testClass)
    {
        fireTestEvent(testCaseStarting(testClass));
    }

    public void addConsoleOutputListener(ConsoleOutputListener listener)
    {
        consoleListenerList.add(listener);
    }

    public void removeConsoleOutputListener(ConsoleOutputListener listener)
    {
        consoleListenerList.remove(listener);
    }

    public void fireTestRunComplete()
    {
        for (TestQueueListener each : testQueueListenerList)
        {
            each.testRunComplete();
        }
    }

    public void fireConsoleUpdateEvent(String newText, OutputType outputType)
    {
        for (ConsoleOutputListener each : consoleListenerList)
        {
            each.consoleOutputUpdate(newText, outputType);
        }
    }

    public void addTestQueueListener(TestQueueListener listener)
    {
        testQueueListenerList.add(listener);
    }

    public void removeTestQueueListener(ReloadListener listener)
    {
        testQueueListenerList.remove(listener);
    }

    public void fireQueueEvent(TestQueueEvent event)
    {
        for (TestQueueListener each : testQueueListenerList)
        {
            each.testQueueUpdated(event);
        }
    }

}
