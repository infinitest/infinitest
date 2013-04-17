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

import java.util.*;

import org.infinitest.*;

abstract class AbstractTestRunner implements TestRunner {
	private final RunnerEventSupport eventSupport;
	private RuntimeEnvironment environment;
	private ConcurrencyController concurrencyController;
	private Comparator<String> testPriority;

	public AbstractTestRunner() {
		eventSupport = new RunnerEventSupport(this);
		testPriority = new NoOpComparator();
	}

	public void setConcurrencyController(ConcurrencyController concurrencyController) {
		this.concurrencyController = concurrencyController;
	}

	protected ConcurrencyController getConcurrencySemaphore() {
		if (concurrencyController == null) {
			concurrencyController = new SingleLockConcurrencyController();
		}
		return concurrencyController;
	}

	protected Comparator<String> getTestPriority() {
		return testPriority;
	}

	public void setTestPriority(Comparator<String> testPriority) {
		this.testPriority = testPriority;
	}

	public void addTestResultsListener(TestResultsListener statusListener) {
		eventSupport.addTestStatusListener(statusListener);
	}

	public void removeTestStatusListener(TestResultsListener statusListener) {
		eventSupport.removeTestStatusListener(statusListener);
	}

	public void addConsoleOutputListener(ConsoleOutputListener listener) {
		eventSupport.addConsoleOutputListener(listener);
	}

	public void removeConsoleOutputListener(ConsoleOutputListener listener) {
		eventSupport.removeConsoleOutputListener(listener);
	}

	public void addTestQueueListener(TestQueueListener listener) {
		eventSupport.addTestQueueListener(listener);
	}

	public void removeTestQueueListener(ReloadListener listener) {
		eventSupport.removeTestQueueListener(listener);
	}

	protected RunnerEventSupport getEventSupport() {
		return eventSupport;
	}

	public void runTest(String testClass) {
		runTests(newArrayList(testClass));
	}

	public void runTests(List<String> testNames) {
		for (String test : testNames) {
			runTest(test);
		}
	}

	public void setRuntimeEnvironment(RuntimeEnvironment environment) {
		this.environment = environment;
	}

	protected RuntimeEnvironment getRuntimeEnvironment() {
		return environment;
	}

	private static class NoOpComparator implements Comparator<String> {
		public int compare(String o1, String o2) {
			return 0;
		}
	}
}
