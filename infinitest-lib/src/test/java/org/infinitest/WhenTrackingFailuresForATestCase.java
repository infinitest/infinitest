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

import static com.google.common.collect.Iterables.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.util.*;

import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenTrackingFailuresForATestCase {
	private TestCaseFailures failures;
	private TestEvent newEvent;

	@Before
	public void inContext() {
		TestEvent method1Event = eventFor("method1");
		failures = new TestCaseFailures(asList(method1Event, eventFor("method2")));
		failures.addNewFailure(method1Event);
	}

	@Test
	public void shouldIgnoreStrictlyEqualUpdates() {
		assertTrue(failures.updatedFailures().isEmpty());
	}

	@Test
	public void shouldOnlyUpdateFailuresIfNotStrictlyEqual() {
		newEvent = failedWithMessage("method2", "anotherMessage");
		failures.addNewFailure(newEvent);
		Collection<TestEvent> updatedFailures = failures.updatedFailures();
		assertSame(newEvent, getOnlyElement(updatedFailures));
	}

	@Test
	public void shouldDetermineWhichFailuresAreNew() {
		failures.addNewFailure(eventFor("method3"));
		Collection<TestEvent> newFailures = failures.newFailures();
		assertThat(newFailures, hasItem(eventFor("method3")));
		assertEquals(1, newFailures.size());
	}

	@Test
	public void shouldDetermineWhichFailuresHaveBeenRemoved() {
		Collection<TestEvent> removedFailures = failures.removedFailures();
		assertEquals(eventFor("method2"), getOnlyElement(removedFailures));
	}

	private TestEvent eventFor(String methodName) {
		return failedWithMessage(methodName, "message");
	}

	private TestEvent failedWithMessage(String methodName, String message) {
		return methodFailed(message, "testName", methodName, new AssertionError(methodName + " failed"));
	}
}
