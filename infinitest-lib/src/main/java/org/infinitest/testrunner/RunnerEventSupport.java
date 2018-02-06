/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static org.infinitest.testrunner.TestEvent.*;

import java.util.*;

import org.infinitest.*;
import org.infinitest.ConsoleOutputListener.OutputType;

public class RunnerEventSupport {
	private final List<ConsoleOutputListener> consoleListenerList;
	private final List<TestQueueListener> testQueueListenerList;
	private final List<TestResultsListener> listeners;
	private final Object source;

	public RunnerEventSupport(Object eventSource) {
		source = eventSource;
		listeners = newArrayList();
		consoleListenerList = newArrayList();
		testQueueListenerList = newArrayList();
	}

	public void addTestStatusListener(TestResultsListener listener) {
		listeners.add(listener);
	}

	public void removeTestStatusListener(TestResultsListener listener) {
		listeners.remove(listener);
	}

	private void fireTestEvent(TestEvent testEvent) {
		for (TestResultsListener each : listeners) {
			switch (testEvent.getType()) {
				case TEST_CASE_STARTING:
					each.testCaseStarting(testEvent);
					break;
				default:
					throw new IllegalArgumentException("Unknown event type:" + testEvent);
			}
		}
	}

	public void fireTestCaseComplete(String testName, TestResults results) {
		for (TestResultsListener each : listeners) {
			each.testCaseComplete(new TestCaseEvent(testName, source, results));
		}
	}

	public void fireStartingEvent(String testClass) {
		TestEvent testEvent = testCaseStarting(testClass);
		fireTestEvent(testEvent);
	}

	public void addConsoleOutputListener(ConsoleOutputListener listener) {
		consoleListenerList.add(listener);
	}

	public void removeConsoleOutputListener(ConsoleOutputListener listener) {
		consoleListenerList.remove(listener);
	}

	public void fireTestRunComplete() {
		for (TestQueueListener each : testQueueListenerList) {
			each.testRunComplete();
		}
	}

	public void fireConsoleUpdateEvent(String newText, OutputType outputType) {
		for (ConsoleOutputListener each : consoleListenerList) {
			each.consoleOutputUpdate(newText, outputType);
		}
	}

	public void addTestQueueListener(TestQueueListener listener) {
		testQueueListenerList.add(listener);
	}

	public void removeTestQueueListener(ReloadListener listener) {
		testQueueListenerList.remove(listener);
	}

	public void fireQueueEvent(TestQueueEvent event) {
		for (TestQueueListener each : testQueueListenerList) {
			each.testQueueUpdated(event);
		}
	}

}
