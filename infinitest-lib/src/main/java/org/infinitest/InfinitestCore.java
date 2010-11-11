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
package org.infinitest;

import java.io.File;
import java.util.Collection;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResultsListener;

/**
 * Each core runs tests for a single project (a collection of tests that all need the same
 * classpath). It can watch one or more class output directories, scanning for tests and changes to
 * class files, and running the appropriate tests as necessary. For a multi module project or
 * workspace, you will need multiple InfinitestCore instances to watch all the sub-modules.
 */
public interface InfinitestCore
{
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
     * Tell the core to look for changes to class files and run tests as appropriate. This call is
     * asynchronous. Events will be fired to publish results. If a test run is in progress, new
     * tests will be added to the current queue.
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

    /**
     * Re-indexes all the classes in the output directory and re-runs all the tests.
     * 
     * @see TestQueueListener#reloading()
     */
    void reload();

    /**
     * Change the runtime environment used to run tests in this core. If it is different than the
     * existing RuntimeEnvironment, this will trigger a complete reload of the core.
     */
    void setRuntimeEnvironment(RuntimeEnvironment environment);

    RuntimeEnvironment getRuntimeEnvironment();

    String getName();

    boolean isEventSourceFor(TestCaseEvent testCaseEvent);

    /**
     * Listens for console output generated from test runs. Note that this event could be fired
     * after the test queue listener says the test run is complete.
     */
    void addConsoleOutputListener(ConsoleOutputListener listener);

    void removeConsoleOutputListener(ConsoleOutputListener listener);

}
