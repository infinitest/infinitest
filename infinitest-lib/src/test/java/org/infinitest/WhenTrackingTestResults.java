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
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import java.util.*;

import junit.framework.*;

import org.infinitest.testrunner.*;
import org.junit.Test;

import com.google.common.collect.*;

public class WhenTrackingTestResults extends ResultCollectorTestSupport {
	private static final String TEST_NAME = "com.fakeco.TestFoo";
	private static final String TEST_METHOD = "someMethod";

	@Test
	public void shouldOrderMostRecentFailuresFirst() {
		TestEvent mostRecentFailure = eventWithError();

		testRun(eventWithError());
		testRun(mostRecentFailure);

		assertEquals(mostRecentFailure.getPointOfFailure(), collector.getPointOfFailure(0));
	}

	@Test
	public void shouldUnifyEventsWithSamePointOfFailure() {
		Throwable pointOfFailure = new AssertionFailedError().fillInStackTrace();

		testRun(eventWithError(pointOfFailure), eventWithError(pointOfFailure), eventWithError(new AssertionError()));

		assertEquals(2, collector.getPointOfFailureCount());
		assertEquals(3, collector.getFailures().size());
	}

	@Test
	public void shouldFireEventsToNotifyListenerWhenTestCaseIsComplete() {
		assertTrue(listener.failures.isEmpty());

		TestEvent failure = eventWithError();
		testRun(failure);
		assertEquals(failure, getOnlyElement(listener.failures));
	}

	@Test
	public void shouldClearResultsWhenStatusChangesToReloading() {
		TestEvent event = eventWithError();
		testRun(event);
		collector.reloading();

		assertEquals(0, collector.getPointOfFailureCount());
		assertFalse(collector.hasFailures());
		assertEquals(event, Iterables.getOnlyElement(listener.removed));
	}

	@Test
	public void shouldIndicateFailures() {
		testRun(eventWithError());

		assertTrue(collector.hasFailures());
	}

	@Test
	public void shouldFireUpdateEventsWhenFailuresChange() {
		TestEvent event = eventWithError();
		testRun(event);
		assertTrue(listener.removed.isEmpty());
		assertTrue(listener.changed.isEmpty());

		listener.clear();
		event = eventWithError();
		testRun(event);

		assertSame(event, getOnlyElement(listener.changed));
		assertTrue(listener.removed.isEmpty());
		assertTrue(listener.added.toString(), listener.added.isEmpty());
	}

	@Test
	public void shouldProvideUniqueSetOfPointsOfFailure() {
		Exception firstException = new Exception("Some message.");
		Exception secondException = new Exception("Some other message.");
		testRun(methodFailed("shouldFoo", "", firstException), methodFailed("shouldBar", "", firstException), methodFailed("shouldBaz", "", secondException));

		assertEquals(2, collector.getPointsOfFailure().size());
	}

	@Test
	public void shouldProvideAllFailuresForSpecificPointOfFailure() {
		Exception firstException = new Exception("Some message.");
		Exception secondException = new Exception("Some other message.");
		testRun(methodFailed("shouldFoo", "", firstException), methodFailed("shouldBar", "", firstException), methodFailed("shouldBaz", "", secondException));

		for (PointOfFailure pointOfFailure : collector.getPointsOfFailure()) {
			if (pointOfFailure.getMessage().contains("other message")) {
				assertEquals(1, collector.getFailuresForPointOfFailure(pointOfFailure).size());
			} else {
				assertEquals(2, collector.getFailuresForPointOfFailure(pointOfFailure).size());
			}
		}
	}

	@Test
	public void canClearAllData() {
		testRun(eventWithError());
		List<TestEvent> failures = collector.getFailures();

		assertEquals(1, failures.size());
		PointOfFailure pointOfFailure = getOnlyElement(failures).getPointOfFailure();
		assertEquals(1, collector.getFailuresForPointOfFailure(pointOfFailure).size());

		collector.clear();
		failures = collector.getFailures();
		assertEquals(0, failures.size());
		assertEquals(0, collector.getFailuresForPointOfFailure(pointOfFailure).size());
	}

	private static TestEvent eventWithError() {
		return eventWithError(new AssertionFailedError());
	}

	private static TestEvent eventWithError(Throwable error) {
		return new TestEvent(METHOD_FAILURE, "", TEST_NAME, TEST_METHOD, error);
	}
}
