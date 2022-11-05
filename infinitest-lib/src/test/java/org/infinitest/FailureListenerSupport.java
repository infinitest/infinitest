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

import java.util.*;

import org.infinitest.testrunner.*;

class FailureListenerSupport implements FailureListListener {
	public final List<TestEvent> added;
	public final List<TestEvent> removed;
	public final List<TestEvent> failures;
	public final List<TestEvent> changed;

	FailureListenerSupport() {
		added = new ArrayList<>();
		removed = new ArrayList<>();
		failures = new ArrayList<>();
		changed = new ArrayList<>();
	}

	@Override
	public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		added.addAll(failuresAdded);
		removed.addAll(failuresRemoved);
		failures.addAll(failuresAdded);
		failures.removeAll(failuresRemoved);
	}

	@Override
	public void failuresUpdated(Collection<TestEvent> updatedFailures) {
		changed.addAll(updatedFailures);
	}

	public void clear() {
		added.clear();
		removed.clear();
		failures.clear();
		changed.clear();
	}
}