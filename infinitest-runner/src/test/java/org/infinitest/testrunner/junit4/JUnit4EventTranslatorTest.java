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
package org.infinitest.testrunner.junit4;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.runner.Description.createTestDescription;

import org.infinitest.testrunner.MethodStats;
import org.infinitest.testrunner.StubClock;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JUnit4EventTranslatorTest {
	private JUnitEventTranslator eventTranslator;
	private Result result;
	private Description description;
	private StubClock stubClock;

	@Before
	public void inContext() throws Exception {
		stubClock = new StubClock();
		eventTranslator = new JUnitEventTranslator(stubClock);
		description = createTestDescription(getClass(), "methodName");
		eventTranslator.testRunStarted(description);
		result = new Result();
	}

	@Test
	public void shouldCollectEventsInTestResults() {
		TestResults results = eventTranslator.getTestResults();
		assertTrue(isEmpty(results));
	}

	@Test
	public void shouldCollectMethodStatistics() {
		stubClock.time = 10;
		eventTranslator.testStarted(description);
		stubClock.time = 20;
		eventTranslator.testFinished(description);
		TestResults results = eventTranslator.getTestResults();
		MethodStats methodStats = getOnlyElement(results.getMethodStats());
		assertEquals(10, methodStats.startTime);
		assertEquals(20, methodStats.stopTime);
	}

	@Test
	public void shouldCollectFailureEvents() throws Exception {
		AssertionError error = new AssertionError();
		result.createListener().testFailure(new Failure(description, error));
		eventTranslator.testRunFinished(result);
		TestEvent expectedEvent = methodFailed("", getClass().getName(), "methodName", error);
		assertEquals(expectedEvent, getOnlyElement(eventTranslator.getTestResults()));
	}

	@Test
	public void shouldCollectErrorEvents() throws Exception {
		Exception error = new NullPointerException();
		result.createListener().testFailure(new Failure(description, error));
		eventTranslator.testRunFinished(result);
		TestEvent expectedEvent = methodFailed("", getClass().getName(), "methodName", error);
		assertEquals(expectedEvent, getOnlyElement(eventTranslator.getTestResults()));
	}
}
