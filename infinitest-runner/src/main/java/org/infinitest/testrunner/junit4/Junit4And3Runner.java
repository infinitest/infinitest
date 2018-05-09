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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.InfinitestConfigurationSource;
import org.infinitest.testrunner.TestResults;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Runner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Runner for Junit 4 and 3
 * @author sarod
 *
 */
public class Junit4And3Runner {

	private final InfinitestConfigurationSource configSource;

	public Junit4And3Runner(InfinitestConfigurationSource configSource) {
		this.configSource = configSource;
	}

	public TestResults runTest(Class<?> classUnderTest) {
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


}
