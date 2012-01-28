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

import static com.google.common.collect.Lists.*;

import java.util.*;

import org.infinitest.testrunner.*;

class FailureListenerSupport implements FailureListListener {
	public final List<TestEvent> added;
	public final List<TestEvent> removed;
	public final List<TestEvent> failures;
	public final List<TestEvent> changed;

	FailureListenerSupport() {
		added = newArrayList();
		removed = newArrayList();
		failures = newArrayList();
		changed = newArrayList();
	}

	public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		added.addAll(failuresAdded);
		removed.addAll(failuresRemoved);
		failures.addAll(failuresAdded);
		failures.removeAll(failuresRemoved);
	}

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