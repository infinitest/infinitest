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
