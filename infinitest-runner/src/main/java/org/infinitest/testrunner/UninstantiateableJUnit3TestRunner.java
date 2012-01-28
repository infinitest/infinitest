/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.testrunner;

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

		public void endTest(Test test) {
			fNotifier.fireTestFinished(asDescription(test));
		}

		public void startTest(Test test) {
			fNotifier.fireTestStarted(asDescription(test));
		}

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
