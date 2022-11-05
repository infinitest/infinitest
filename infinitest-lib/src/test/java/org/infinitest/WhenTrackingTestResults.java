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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterables;

import junit.framework.AssertionFailedError;

class WhenTrackingTestResults extends ResultCollectorTestSupport {
	private static final String TEST_NAME = "com.fakeco.TestFoo";
	private static final String TEST_METHOD = "someMethod";

	@Test
	void shouldOrderMostRecentFailuresFirst() {
		TestEvent mostRecentFailure = eventWithError();

		testRun(eventWithError());
		testRun(mostRecentFailure);

		assertEquals(mostRecentFailure.getPointOfFailure(), collector.getPointOfFailure(0));
	}

	@Test
	void shouldUnifyEventsWithSamePointOfFailure() {
		Throwable pointOfFailure = new AssertionFailedError().fillInStackTrace();

		testRun(eventWithError(pointOfFailure), eventWithError(pointOfFailure), eventWithError(new AssertionError()));

		assertEquals(2, collector.getPointOfFailureCount());
		assertEquals(3, collector.getFailures().size());
	}

	@Test
	void shouldFireEventsToNotifyListenerWhenTestCaseIsComplete() {
		assertTrue(listener.failures.isEmpty());

		TestEvent failure = eventWithError();
		testRun(failure);
		assertEquals(failure, getOnlyElement(listener.failures));
	}

	@Test
	void shouldClearResultsWhenStatusChangesToReloading() {
		TestEvent event = eventWithError();
		testRun(event);
		collector.reloading();

		assertEquals(0, collector.getPointOfFailureCount());
		assertFalse(collector.hasFailures());
		assertEquals(event, Iterables.getOnlyElement(listener.removed));
	}

	@Test
	void shouldIndicateFailures() {
		testRun(eventWithError());

		assertTrue(collector.hasFailures());
	}

	@Test
	void shouldFireUpdateEventsWhenFailuresChange() {
		TestEvent event = eventWithError();
		testRun(event);
		assertThat(listener.removed).isEmpty();
		assertThat(listener.changed).isEmpty();

		listener.clear();
		event = eventWithError();
		testRun(event);

		assertSame(event, getOnlyElement(listener.changed));
		assertThat(listener.removed).isEmpty();
		assertThat(listener.added).as(listener.added.toString()).isEmpty();
	}

	@Test
	void shouldProvideUniqueSetOfPointsOfFailure() {
		Exception firstException = new Exception("Some message.");
		Exception secondException = new Exception("Some other message.");
		testRun(methodFailed("shouldFoo", "", firstException), methodFailed("shouldBar", "", firstException), methodFailed("shouldBaz", "", secondException));

		assertEquals(2, collector.getPointsOfFailure().size());
	}

	@Test
	void shouldProvideAllFailuresForSpecificPointOfFailure() {
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
	void canClearAllData() {
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
