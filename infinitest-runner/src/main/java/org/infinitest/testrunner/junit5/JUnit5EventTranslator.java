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
			getMethodStats(testIdentifier).startTime = clock.currentTimeMillis();
		}
	}

	@Override
	public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
		if (testIdentifier.isTest()) {
			getMethodStats(testIdentifier).stopTime = clock.currentTimeMillis();
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