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

import junit.framework.*;

import org.junit.runner.*;
import org.junit.runner.notification.*;

/**
 * JUnit38ClassRunner is not extensible, so I had to make a copy :-(
 * 
 * @author bjrady
 */
public class UninstantiateableJUnit3TestRunner extends Runner {
	private static final class OldTestClassAdaptingListener implements TestListener {
		private final RunNotifier fNotifier;
		private final Class<?> testClass;

		private OldTestClassAdaptingListener(RunNotifier notifier, Class<?> testClass) {
			fNotifier = notifier;
			this.testClass = testClass;
		}

		@Override
		public void endTest(Test test) {
			fNotifier.fireTestFinished(asDescription(test));
		}

		@Override
		public void startTest(Test test) {
			fNotifier.fireTestStarted(asDescription(test));
		}

		@Override
		public void addError(Test test, Throwable t) {
			Failure failure = new Failure(asDescription(test), t);
			fNotifier.fireTestFailure(failure);
		}

		private Description asDescription(Test test) {
			if (test instanceof JUnit4TestCaseFacade) {
				JUnit4TestCaseFacade facade = (JUnit4TestCaseFacade) test;
				return facade.getDescription();
			}
			return Description.createTestDescription(testClass, "<init>");
		}

		@Override
		public void addFailure(Test test, AssertionFailedError t) {
			addError(test, t);
		}
	}

	private final Test fTest;
	private final Class<?> testClass;

	public UninstantiateableJUnit3TestRunner(Class<?> klass) {
		super();
		fTest = new TestSuite(klass.asSubclass(TestCase.class));
		testClass = klass;
	}

	@Override
	public void run(RunNotifier notifier) {
		TestResult result = new TestResult();
		result.addListener(createAdaptingListener(notifier, testClass));
		fTest.run(result);
	}

	public static TestListener createAdaptingListener(RunNotifier notifier, Class<?> testClass) {
		return new OldTestClassAdaptingListener(notifier, testClass);
	}

	@Override
	public Description getDescription() {
		return Description.EMPTY;
	}
}
