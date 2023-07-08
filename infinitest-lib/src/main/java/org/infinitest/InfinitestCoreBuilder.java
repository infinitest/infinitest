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

import static com.google.common.base.Preconditions.checkNotNull;

import org.infinitest.changedetect.FileChangeDetector;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.filter.RegexFileFilter;
import org.infinitest.filter.TestFilter;
import org.infinitest.parser.ClassFileTestDetector;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.MultiProcessRunner;
import org.infinitest.testrunner.TestRunner;

/**
 * Used to create instances of an {@link InfinitestCore}.
 * 
 * @author bjrady
 */
public class InfinitestCoreBuilder {
	private TestFilter filterList;
	private final RuntimeEnvironment runtimeEnvironment;
	private final EventQueue eventQueue;
	private String coreName;
	private ConcurrencyController controller;

	public InfinitestCoreBuilder(RuntimeEnvironment environment, EventQueue eventQueue, String coreName) {
		checkNotNull(environment, "No runtime environment is configured. Maybe because " + coreName + " has no jdk.");

		runtimeEnvironment = environment;
		this.eventQueue = eventQueue;
		this.coreName = coreName;
		
		filterList = new RegexFileFilter(environment.getConfigurationSource());
		controller = new SingleLockConcurrencyController();
	}

	/**
	 * Creates a new core from the existing builder settings.
	 */
	public InfinitestCore createCore() {
		TestRunner runner = new MultiProcessRunner();
		runner.setConcurrencyController(controller);
		DefaultInfinitestCore core = new DefaultInfinitestCore(runner, eventQueue);
		core.setName(coreName);
		core.setChangeDetector(new FileChangeDetector());
		core.setTestDetector(createTestDetector(filterList));
		core.setRuntimeEnvironment(runtimeEnvironment);
		return core;
	}

	protected TestDetector createTestDetector(TestFilter testFilterList) {
		return new ClassFileTestDetector(testFilterList);
	}

	/**
	 * Sets a test filter. The default filter uses a list of regular expressions
	 * extracted from a file in the project working directory called
	 * infinitest.filters
	 */
	public void setFilter(TestFilter testFilter) {
		filterList = testFilter;
	}

	public void setUpdateSemaphore(ConcurrencyController semaphore) {
		controller = semaphore;
	}
}
