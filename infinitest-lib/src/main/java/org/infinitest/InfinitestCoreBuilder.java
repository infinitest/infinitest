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

import static com.google.common.base.Preconditions.*;

import java.io.*;

import org.infinitest.changedetect.*;
import org.infinitest.filter.*;
import org.infinitest.parser.*;
import org.infinitest.testrunner.*;

/**
 * Used to create instances of an {@link InfinitestCore}.
 * 
 * @author bjrady
 */
public class InfinitestCoreBuilder {
	private TestFilter filterList;
	private final Class<? extends TestRunner> runnerClass;
	private final RuntimeEnvironment runtimeEnvironment;
	private final EventQueue eventQueue;
	private String coreName = "";
	private ConcurrencyController controller;

	public InfinitestCoreBuilder(RuntimeEnvironment environment, EventQueue eventQueue) {
		checkNotNull(environment, "No runtime environment is configured. Maybe because the project has no jdk.");

		runtimeEnvironment = environment;
		this.eventQueue = eventQueue;
		filterList = new RegexFileFilter(new File(environment.getWorkingDirectory(), "infinitest.filters"));
		runnerClass = MultiProcessRunner.class;
		controller = new SingleLockConcurrencyController();
	}

	/**
	 * Creates a new core from the existing builder settings.
	 */
	public InfinitestCore createCore() {
		TestRunner runner = createRunner();
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

	private TestRunner createRunner() {
		try {
			return runnerClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Cannot create runner from class " + runnerClass + ". Did you provide a no-arg constructor?", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot access runner class " + runnerClass, e);
		}
	}

	public void setName(String coreName) {
		this.coreName = coreName;
	}

	public void setUpdateSemaphore(ConcurrencyController semaphore) {
		controller = semaphore;
	}
}
