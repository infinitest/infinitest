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
package org.infinitest.testrunner.testng;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.infinitest.TestNGConfiguration;
import org.infinitest.TestNGConfigurator;
import org.infinitest.config.InfinitestConfigurationSource;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestNG;

public class TestNgRunner {
	
	private final InfinitestConfigurationSource configSource;

	public TestNgRunner(InfinitestConfigurationSource configSource) {
		this.configSource = configSource;		
	}
	

	public static boolean isTestNGTest(Class<?> clazz) {
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

	
	public TestResults runTest(Class<?> classUnderTest) {
		TestNGEventTranslator eventTranslator = new TestNGEventTranslator();

		TestNG core = new TestNG();
		// TestNG 6 has addListener(ITestListener)
		// TestNG 7 has addListener(ITestNGListener)
		// The method is selected at compile time so compiling with either version yields a NoSuchMethodError on the other version
		// Since addListener(Object) is in both versions we use it even though it is deprecated to remain compatible with TestNG 
 		core.addListener((Object) eventTranslator);
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
			for (ITestNGListener listener : config.getListeners()) {
				core.addListener((Object) listener);
			}
		}
	}
	
	private static class TestNGEventTranslator implements ITestListener {
		private final List<TestEvent> events = new ArrayList<>();

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
