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
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.runner.Request.*;

import java.lang.reflect.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.config.*;
import org.junit.experimental.categories.Categories.*;
import org.junit.runner.*;
import org.testng.*;

import junit.framework.*;

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
		} else {
			return runJUnitTest(testClass);
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
