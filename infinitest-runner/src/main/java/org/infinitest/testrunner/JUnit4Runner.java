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
package org.infinitest.testrunner;

import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.runner.Request.*;

import java.lang.reflect.*;
import java.util.*;

import org.infinitest.*;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.runner.*;
import org.testng.*;

/**
 * A proxy which delegates actual test execution JUnit or TestNG.
 *
 * TODO: we should either rename this class or split it up in
 * two classes with the respective JUnit/TestNG functionality.
 */
public class JUnit4Runner implements NativeRunner {
	private TestNGConfiguration config;

	public void setTestNGConfiguration(TestNGConfiguration configuration) {
		config = configuration;
	}

	@Override
	public TestResults runTest(String testClass) {
		Class<?> clazz;
		try {
			clazz = Class.forName(testClass);
		} catch (ClassNotFoundException e) {
			throw new MissingClassException(testClass);
		}

		if (isTestNGTest(clazz)) {
			return runTestNGTest(clazz);
		} else if (isJUnit5Test(clazz)) {
			return runJUnit5Test(clazz);
		} else {
			return runJUnitTest(clazz);
		}
	}

	private TestResults runJUnit5Test(Class<?> clazz) {
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors(selectClass(clazz)).filters(EngineFilter.includeEngines("junit-jupiter"))
				.build();

		Launcher launcher = LauncherFactory.create();

		// Register a listener of your choice
		JUnit5EventTranslator listener = new JUnit5EventTranslator();
		launcher.registerTestExecutionListeners(listener);

		launcher.execute(request, new TestExecutionListener[0]);

		return listener.getTestResults();
	}

	public static boolean isJUnit5Test(Class<?> clazz) {
		try {
			Class.forName(JupiterTestEngine.class.getCanonicalName());
		} catch (ClassNotFoundException e) {
			throw new AssertionError("Jupiter engine not found");
		}
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors( selectClass(clazz) ).filters(EngineFilter.includeEngines("junit-jupiter"))
				.build();
		TestPlan plan = LauncherFactory.create().discover(request);
		long numberOfTests = plan.countTestIdentifiers(t -> t.isTest());
		boolean testsPresent = numberOfTests > 0;
		return testsPresent;
	}

	private TestResults runTestNGTest(Class<?> clazz) {
		TestNGEventTranslator eventTranslator = new TestNGEventTranslator();

		TestNG core = new TestNG();
		core.addListener(eventTranslator);
		core.setTestClasses(new Class[]{clazz});
		if (config == null) {
			config = new TestNGConfigurator().readConfig();
		}
		core.setExcludedGroups(config.getExcludedGroups());
		core.setGroups(config.getGroups());
		if (config.getListeners() != null) {
			for (Object listener : config.getListeners()) {
				core.addListener(listener);
			}
		}

		core.run();

		return eventTranslator.getTestResults();
	}

	private TestResults runJUnitTest(Class<?> clazz) {
		EventTranslator eventTranslator = new EventTranslator();

		JUnitCore core = new JUnitCore();
		core.addListener(eventTranslator);
		core.run(classWithoutSuiteMethod(clazz));
		return eventTranslator.getTestResults();
	}

	private static boolean isTestNGTest(Class<?> clazz) {
		if (containsTestNGTestAnnotation(clazz)) {
			return true;
		}
		for (Method method : clazz.getMethods()) {
			if (containsTestNGTestAnnotation(method)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsTestNGTestAnnotation(AnnotatedElement annotatedElement) {
		return annotatedElement.getAnnotation(org.testng.annotations.Test.class) != null;
	}

	private static class JUnit5EventTranslator implements TestExecutionListener {
		private final List<TestEvent> eventsCollected;
		private final Map<TestIdentifier, MethodStats> methodStats;
		private final Clock clock;

		public JUnit5EventTranslator() {
			this.clock = new SystemClock();
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
					return ((MethodSource)testSource).getClassName();
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
					return ((MethodSource)testSource).getMethodName();
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

	private static class TestNGEventTranslator implements ITestListener {
		private final List<TestEvent> events = new ArrayList<TestEvent>();

		@Override
		public void onTestFailure(ITestResult failure) {
			events.add(createEventFrom(failure));
		}

		private TestEvent createEventFrom(ITestResult failure) {
			return TestEvent.methodFailed(failure.getTestClass().getName(), failure.getName(), failure.getThrowable());
		}

		public TestResults getTestResults() {
			return new TestResults(events);
		}

		@Override
		public void onTestStart(ITestResult result) {
		}

		@Override
		public void onTestSuccess(ITestResult result) {
		}

		@Override
		public void onTestSkipped(ITestResult result) {
		}

		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		}

		@Override
		public void onStart(ITestContext context) {
		}

		@Override
		public void onFinish(ITestContext context) {
		}
	}
}
