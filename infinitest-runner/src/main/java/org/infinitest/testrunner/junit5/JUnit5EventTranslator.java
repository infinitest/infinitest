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
package org.infinitest.testrunner.junit5;

import static org.infinitest.testrunner.TestEvent.methodFailed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infinitest.testrunner.Clock;
import org.infinitest.testrunner.MethodStats;
import org.infinitest.testrunner.SystemClock;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

class JUnit5EventTranslator implements TestExecutionListener {
	private final List<TestEvent> eventsCollected;
	private final Map<TestIdentifier, MethodStats> methodStats;
	private final Clock clock;

	public JUnit5EventTranslator() {
		this(new SystemClock());
	}
	
	public JUnit5EventTranslator(Clock clock) {
		this.clock = clock;
		eventsCollected = new ArrayList<TestEvent>();
		methodStats = new HashMap<TestIdentifier, MethodStats>();
	}

	@Override
	public void executionStarted(TestIdentifier testIdentifier) {
		if (testIdentifier.isTest()) {
			getMethodStats(testIdentifier).start(clock.currentTimeMillis());
		}
	}

	@Override
	public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
		if (testIdentifier.isTest()) {
			getMethodStats(testIdentifier).stop(clock.currentTimeMillis());
			switch (testExecutionResult.getStatus()) {

			case SUCCESSFUL:
				break;

			case ABORTED: {
				break;
			}

			case FAILED: {
				eventsCollected.add(createEventFrom(testIdentifier, testExecutionResult));
				break;
			}

			default:
				throw new PreconditionViolationException(
						"Unsupported execution status:" + testExecutionResult.getStatus());
			}
		}
	}

	private TestEvent createEventFrom(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
		String testCaseName = getTestCaseName(testIdentifier);
		Throwable exception = testExecutionResult.getThrowable().get();

		return methodFailed(testCaseName, getMethodName(testIdentifier), exception);
	}

	private static String getTestCaseName(TestIdentifier description) {
		try {
			TestSource testSource = description.getSource().get();
			if (testSource instanceof MethodSource) {
				return ((MethodSource) testSource).getClassName();
			}
			return description.getDisplayName().split("\\(|\\)")[1];
		} catch (Exception e) {
			return description.getDisplayName();
		}
	}

	public TestResults getTestResults() {
		TestResults results = new TestResults(eventsCollected);
		results.addMethodStats(methodStats.values());
		return results;
	}

	private String getMethodName(TestIdentifier description) {
		try {
			TestSource testSource = description.getSource().get();
			if (testSource instanceof MethodSource) {
				return ((MethodSource) testSource).getMethodName();
			}
			return description.getDisplayName().split("\\(|\\)")[0];
		} catch (Exception e) {
			return description.getDisplayName();
		}
	}

	private MethodStats getMethodStats(TestIdentifier description) {
		MethodStats stats = methodStats.get(description);
		if (stats == null) {
			stats = new MethodStats(getMethodName(description));
			methodStats.put(description, stats);
		}
		return stats;
	}

}