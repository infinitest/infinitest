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

@SuppressWarnings("all")
public class FakeInfinitestCore implements InfinitestCore {
	@Override
	public void addTestQueueListener(TestQueueListener listener) {
	}

	@Override
	public void removeTestQueueListener(TestQueueListener listener) {
	}

	@Override
	public void addTestResultsListener(TestResultsListener listener) {
	}

	@Override
	public void removeTestResultsListener(TestResultsListener listener) {
	}

	@Override
	public int update() {
		return 0;
	}

	@Override
	public void reload() {
	}

	@Override
	public void setRuntimeEnvironment(RuntimeEnvironment environment) {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void addConsoleOutputListener(ConsoleOutputListener listener) {
	}

	@Override
	public void removeConsoleOutputListener(ConsoleOutputListener listener) {
	}

	public void addReloadListener(ReloadListener listener) {
	}

	@Override
	public void addDisabledTestListener(DisabledTestListener listener) {
	}

	@Override
	public void removeDisabledTestListener(DisabledTestListener anyObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public RuntimeEnvironment getRuntimeEnvironment() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEventSourceFor(TestCaseEvent testCaseEvent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Collection<File> changedFiles) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void remove(Collection<File> removedFiles, Set<JavaClass> removedClasses) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void filterFileWasUpdated() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void stop() {
		throw new UnsupportedOperationException();
	}
}
