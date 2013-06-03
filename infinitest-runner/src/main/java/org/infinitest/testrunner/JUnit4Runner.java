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

import static org.junit.runner.Request.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import junit.framework.*;

import org.infinitest.*;
import org.junit.runner.*;
import org.testng.*;

public class JUnit4Runner implements NativeRunner {
	private TestNGConfiguration config = null;

	@Override
	public TestResults runTest(String testClass) {
		Class<?> clazz;
		try {
			clazz = Class.forName(testClass);
		} catch (ClassNotFoundException e) {
			throw new MissingClassException(testClass);
		}

		if (isTestNGTest(clazz)) {
			TestNG core = new TestNG();
			TestNGEventTranslator eventTranslator = new TestNGEventTranslator();
			core.addListener(eventTranslator);

			core.setTestClasses(new Class[]{clazz});
			addTestNGSettings(core);
			core.run();

			return eventTranslator.getTestResults();
		}

		JUnitCore core = new JUnitCore();
		EventTranslator eventTranslator = new EventTranslator();
		core.addListener(eventTranslator);

		if (isJUnit3TestCase(clazz) && cannotBeInstantiated(clazz)) {
			core.run(new UninstantiableJUnit3TestRequest(clazz));
		} else {
			core.run(classWithoutSuiteMethod(clazz));
		}
		return eventTranslator.getTestResults();
	}

	private void addTestNGSettings(TestNG core) {
		if (config == null) {
			config = new TestNGConfigurator().getConfig();
		}
		core.setExcludedGroups(config.getExcludedGroups());
		core.setGroups(config.getGroups());
		setListeners(core);
	}

	private void setListeners(TestNG core) {
		if (config.getListeners() != null) {
			for (Object listener : config.getListeners()) {
				core.addListener(listener);
			}
		}
	}

	private boolean isJUnit3TestCase(Class<?> clazz) {
		return TestCase.class.isAssignableFrom(clazz);
	}

	private boolean cannotBeInstantiated(Class<?> clazz) {
		CustomTestSuite testSuite = new CustomTestSuite(clazz.asSubclass(TestCase.class));
		return testSuite.hasWarnings();
	}

	private static class CustomTestSuite extends TestSuite {
		public CustomTestSuite(Class<? extends TestCase> testClass) {
			super(testClass);
		}

		private boolean hasWarnings() {
			for (Enumeration<Test> tests = tests(); tests.hasMoreElements(); ) {
				Test test = tests.nextElement();
				if (test instanceof TestCase) {
					TestCase testCase = (TestCase) test;
					if (testCase.getName().equals("warning")) {
						return true;
					}
				}
			}
			return false;
		}
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

	private boolean isTestNGTest(Class<?> clazz) {
		if (containsTestNGTestAnnotation(clazz.getAnnotations())) {
			return true;
		}
		for (Method method : clazz.getMethods()) {
			if (containsTestNGTestAnnotation(method.getAnnotations())) {
				return true;
			}
		}
		return false;
	}

	private boolean containsTestNGTestAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == org.testng.annotations.Test.class) {
				return true;
			}
		}
		return false;
	}

	static class TestNGEventTranslator implements ITestListener {
		private final List<TestEvent> eventsCollected = new ArrayList<TestEvent>();

		@Override
		public void onTestStart(ITestResult result) {
		}

		@Override
		public void onTestSuccess(ITestResult result) {
		}

		@Override
		public void onTestFailure(ITestResult failure) {
			eventsCollected.add(createEventFrom(failure));
		}

		private TestEvent createEventFrom(ITestResult failure) {
			return TestEvent.methodFailed(failure.getTestClass().getName(), failure.getName(), failure.getThrowable());
		}

		public TestResults getTestResults() {
			return new TestResults(eventsCollected);
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

	public void setTestNGConfiguration(TestNGConfiguration configuration) {
		config = configuration;
	}
}
