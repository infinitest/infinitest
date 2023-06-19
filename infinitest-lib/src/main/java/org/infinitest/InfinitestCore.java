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
package org.infinitest;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.parser.JavaClass;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResultsListener;

/**
 * Each core runs tests for a single project (a collection of tests that all
 * need the same classpath). It can watch one or more class output directories,
 * scanning for tests and changes to class files, and running the appropriate
 * tests as necessary. For a multi module project or workspace, you will need
 * multiple InfinitestCore instances to watch all the sub-modules.
 */
public interface InfinitestCore {
	/**
	 * Subscribe to this event to be notified about changes in the test queue.
	 */
	void addTestQueueListener(TestQueueListener listener);

	void removeTestQueueListener(TestQueueListener listener);

	/**
	 * Subscribe to this event to be notified about test failures and successes
	 */
	void addTestResultsListener(TestResultsListener listener);

	void removeTestResultsListener(TestResultsListener listener);

	void addDisabledTestListener(DisabledTestListener listener);

	void removeDisabledTestListener(DisabledTestListener listener);

	/**
	 * Tell the core to look for changes to class files and run tests as
	 * appropriate. This call is asynchronous. Events will be fired to publish
	 * results. If a test run is in progress, new tests will be added to the
	 * current queue.
	 * <p/>
	 * 
	 * This method should always be called from the main UI event queue thread.
	 * 
	 * @see EventQueue
	 */
	int update();

	/**
	 * Uses a list of changed files instead of searching for them
	 */
	int update(Collection<File> changedFiles);
	
	void remove(Collection<File> removedFiles, Set<JavaClass> removedClasses);

	/**
	 * Re-indexes all the classes in the output directory and re-runs all the
	 * tests.
	 * 
	 * @see TestQueueListener#reloading()
	 */
	void reload();

	/**
	 * Change the runtime environment used to run tests in this core. If it is
	 * different than the existing RuntimeEnvironment, this will trigger a
	 * complete reload of the core.
	 */
	void setRuntimeEnvironment(RuntimeEnvironment environment);

	RuntimeEnvironment getRuntimeEnvironment();

	String getName();

	boolean isEventSourceFor(TestCaseEvent testCaseEvent);

	/**
	 * Listens for console output generated from test runs. Note that this event
	 * could be fired after the test queue listener says the test run is
	 * complete.
	 */
	void addConsoleOutputListener(ConsoleOutputListener listener);

	void removeConsoleOutputListener(ConsoleOutputListener listener);

	/**
	 * To be called when the filter file was modified, created or deleted
	 */
	void filterFileWasUpdated();

	/**
	 * Stop the tests that might be currently running
	 */
	void stop();
}
