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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infinitest.MissingClassException;
import org.infinitest.TestNGConfiguration;
import org.infinitest.TestNGConfigurator;
import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.InfinitestConfigurationSource;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A proxy which delegates actual test execution JUnit or TestNG.
 *
 * TODO: we should either rename this class or split it up in two classes with
 * the respective JUnit/TestNG functionality.
 */
public class JUnit4Runner implements NativeRunner {
	private InfinitestConfigurationSource configSource = FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory();

	// to override the default config source for unit tests
	public void setTestConfigurationSource(InfinitestConfigurationSource configurationSource) {
		configSource = configurationSource;
	}

	@Override
	public TestResults runTest(String testClassName) {
		Class<?> testClass;
		try {
			testClass = Class.forName(testClassName);
		} catch (ClassNotFoundException e) {
			throw new MissingClassException(testClassName);
		}

		if (isTestNGTest(testClass)) {
			return runTestNGTest(testClass);
		} else if (isJUnit5Test(testClass)) {
			return runJUnit5Test(testClass);
		} else {
			return runJUnitTest(testClass);
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
			Class<?> testEngineClass = Class.forName(JupiterTestEngine.class.getCanonicalName());
		} catch (ClassNotFoundException e) {
			throw new AssertionError("Jupiter engine not found");
		}
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
				.selectors( selectClass(clazz) ).filters(EngineFilter.includeEngines("junit-jupiter"))
				.build();

		try {
			TestPlan plan = LauncherFactory.create().discover(request);
			long numberOfTests = plan.countTestIdentifiers(t -> t.isTest());
			boolean testsPresent = numberOfTests > 0;
			return testsPresent;
		} catch (Throwable e) {
			// This might fail for any number of reasons...
			// TODO : use some log instead?
			e.printStackTrace();
			return false;
		}
	}
	

	private TestResults runTestNGTest(Class<?> classUnderTest) {
		TestNGEventTranslator eventTranslator = new TestNGEventTranslator();

		TestNG core = new TestNG();
		core.addListener(eventTranslator);
		core.setTestClasses(new Class[] { classUnderTest });

		TestNGConfiguration config = new TestNGConfigurator(configSource).readConfig();

		applyConfig(core, config);

		core.run();

		return eventTranslator.getTestResults();
	}

	private void applyConfig(TestNG core, TestNGConfiguration config) {
		core.setExcludedGroups(config.getExcludedGroups());
		core.setGroups(config.getGroups());
		if (config.getListeners() != null) {
			for (Object listener : config.getListeners()) {
				core.addListener(listener);
			}
		}
	}

	private TestResults runJUnitTest(Class<?> classUnderTest) {
		JUnitEventTranslator eventTranslator = new JUnitEventTranslator();

		JUnitCore core = new JUnitCore();
		core.addListener(eventTranslator);

		core.run(junitTestsToRunFrom(classUnderTest));

		return eventTranslator.getTestResults();
	}

	private Request junitTestsToRunFrom(Class<?> classUnderTest) {
		if (isJUnit3TestCaseWithWarnings(classUnderTest)) {
			return new UninstantiableJUnit3TestRequest(classUnderTest);
		}

		Request request = Request.classWithoutSuiteMethod(classUnderTest);

		Class<?>[] junitCategoriesToExclude = readExcludedGroupsFromConfiguration();

		CategoryFilter excludeCategoriesFilter = CategoryFilter.exclude(junitCategoriesToExclude);

		return request.filterWith(excludeCategoriesFilter);
	}

	/**
	 * we use the TestNGConfigurator do this kind of work for TestNG, but
	 * there's only one configuration option that we're looking at for JUnit at
	 * the moment (excludedGroups) so it's simpler to just leave it in one
	 * method for now.
	 */
	private Class<?>[] readExcludedGroupsFromConfiguration() {
		InfinitestConfiguration configuration = configSource.getConfiguration();

		List<Class<?>> categoriesToExclude = new ArrayList<Class<?>>();
		for (String excludedGroup : configuration.excludedGroups()) {
			try {
				categoriesToExclude.add(Class.forName(excludedGroup));
			} catch (ClassNotFoundException e) {
				// can't find the specified class so log it and keep looking for
				// the others
				e.printStackTrace();
			}
		}

		return categoriesToExclude.toArray(new Class<?>[0]);
	}

	private boolean isJUnit3TestCaseWithWarnings(Class<?> classUnderTest) {
		if (!TestCase.class.isAssignableFrom(classUnderTest)) {
			return false;
		}

		List<Test> tests = Collections.list(new TestSuite(classUnderTest).tests());

		for (Test test : tests) {
			if (test instanceof TestCase) {
				if (((TestCase) test).getName().equals("warning")) {
					return true;
				}
			}
		}

		return false;
	}

	private static class UninstantiableJUnit3TestRequest extends Request {
		private final Class<?> testClass;

		public UninstantiableJUnit3TestRequest(Class<?> clazz) {
			testClass = clazz;
		}

		@Override
		public Runner getRunner() {
			return new UninstantiateableJUnit3TestRunner(testClass);
		}
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
