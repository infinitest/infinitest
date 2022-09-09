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

import static org.infinitest.testrunner.TestEvent.*;

import java.util.*;

import org.infinitest.testrunner.Clock;
import org.infinitest.testrunner.MethodStats;
import org.infinitest.testrunner.SystemClock;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.runner.*;
import org.junit.runner.notification.*;

/**
 * A listener for JUnit lifecycle events during test execution.
 */
class JUnitEventTranslator extends RunListener {
	private final List<TestEvent> eventsCollected;
	private final Map<Description, MethodStats> methodStats;
	private final Clock clock;

	JUnitEventTranslator(Clock clock) {
		this.clock = clock;
		eventsCollected = new ArrayList<TestEvent>();
		methodStats = new HashMap<Description, MethodStats>();
	}

	JUnitEventTranslator() {
		this(new SystemClock());
	}

	@Override
	public void testStarted(Description description) {
		getMethodStats(description).start(clock.currentTimeMillis());
	}

	@Override
	public void testFinished(Description description) {
		getMethodStats(description).stop(clock.currentTimeMillis());
	}

	@Override
	public void testRunFinished(Result result) {
		for (Failure failure : result.getFailures()) {
			TestEvent event = createEventFrom(failure);
			if (notCausedByFindingNoTests(event)) {
				eventsCollected.add(event);
			}
		}
	}

	private boolean notCausedByFindingNoTests(TestEvent event) {
		// if we try to run a test class but all of the test methods have been
		// excluded by category filters, JUnit will still report a failure.
		// It's not really a failure for our purposes, so we'll ignore it.
		return !event.getMessage().startsWith("No tests found matching categories");
	}

	public TestResults getTestResults() {
		TestResults results = new TestResults(eventsCollected);
		results.addMethodStats(methodStats.values());
		return results;
	}

	private TestEvent createEventFrom(Failure failure) {
		Description testDescription = failure.getDescription();
		String testCaseName = getTestCaseName(testDescription);
		Throwable exception = failure.getException();
		String message = failure.getMessage();
		if (null == message) {
			message = "";
		}

		return methodFailed(message, testCaseName, getMethodName(testDescription), exception);
	}

	private static String getTestCaseName(Description description) {
		try {
			return description.getDisplayName().split("\\(|\\)")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return description.getDisplayName();
		}
	}

	private String getMethodName(Description description) {
		return description.getDisplayName().split("\\(|\\)")[0];
	}

	private MethodStats getMethodStats(Description description) {
		MethodStats stats = methodStats.get(description);
		if (stats == null) {
			stats = new MethodStats(getMethodName(description));
			methodStats.put(description, stats);
		}
		return stats;
	}
}
