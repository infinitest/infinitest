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

import static com.google.common.collect.Iterables.*;
import static org.junit.Assert.*;

import java.util.*;

import org.infinitest.*;
import org.junit.*;
import org.testng.*;
import org.testng.reporters.*;

public class WhenRunningTestNGTests {
	private JUnit4Runner runner;
	private static final String CLASS_UNDER_TEST = TestWithTestNGGroups.class.getName();
	private TestNGConfiguration config;
	private boolean wasCalled = false;

	@Before
	public void inContext() {
		runner = new JUnit4Runner();
		config = new TestNGConfiguration();
		runner.setTestNGConfiguration(config);
		TestWithTestNGGroups.fail = true;
		TestWithTestNGGroups.dependencyFail = true;
		wasCalled = false;
	}

	@After
	public void cleanup() {
		TestWithTestNGGroups.fail = false;
		TestWithTestNGGroups.dependencyFail = false;
		runner.setTestNGConfiguration(null);
	}

	/**
	 * no test filters set: bad tests fail. But: the dependent test
	 * "shouldNoBeTestedDueToDependencyOnFilteredGroup" is not executed
	 */
	@Test
	public void shouldFailIfBadTestsAreNotFiltered() {
		final Set<String> failingMethods = new HashSet<String>(Arrays.asList("shouldNotBeTestedGroup", "shouldNotBeTestedGroup3", "shouldNotBeTestedGroup2"));
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		int counter = 0;
		for (TestEvent testEvent : results) {
			counter++;
			assertTrue(failingMethods.contains(testEvent.getTestMethod()));
			assertEquals(AssertionError.class.getName(), testEvent.getFullErrorClassName());
			assertEquals(CLASS_UNDER_TEST, testEvent.getTestName());
		}
		assertEquals(failingMethods.size(), counter);
	}

	@Test
	public void shouldExecuteDependentTestIfMasterGroupWorked() {
		TestWithTestNGGroups.fail = false;
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		TestEvent testEvent = getOnlyElement(results);
		assertEquals("shouldNoBeTestedDueToDependencyOnFilteredGroup", testEvent.getTestMethod());
		assertEquals(AssertionError.class.getName(), testEvent.getFullErrorClassName());
	}

	@Test
	public void shouldNotFailWithFilteredGroupsSet() {
		config.setExcludedGroups("slow, manual, green");
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(0, size(results));
	}

	@Test
	public void shouldExecuteOnlyTheSpecifiedGroup() {
		config.setGroups("slow");
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(2, size(results));

		config.setGroups("shouldbetested");
		results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(0, size(results));
	}

	/**
	 * if the group "slow" is included, but "mixed" excluded, a test with groups
	 * = "mixed", "slow" will not be executed
	 */
	@Test
	public void combineIncludedAndExcludedGroups() {
		config.setGroups("slow");
		config.setExcludedGroups("mixed");
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(1, size(results));
	}

	@Test
	public void shouldReactToListener() {
		MyJUnitXMLReporter testListener = new MyJUnitXMLReporter();
		List<Object> reporters = new ArrayList<Object>();
		reporters.add(testListener);
		config.setListeners(reporters);
		runner.runTest(CLASS_UNDER_TEST);
		assertTrue(wasCalled);
	}

	@Test
	public void should_call_setup_if_one_of_its_group_is_activated() {
		TestWithTestNGGroupsAndSetup.setupWasCalled = false;

		config.setGroups("automated");
		TestResults results = runner.runTest(TestWithTestNGGroupsAndSetup.class.getName());

		assertTrue(TestWithTestNGGroupsAndSetup.setupWasCalled);
		assertEquals(1, size(results));
	}

	@Test
	public void should_NOT_call_setup_if_one_of_its_group_is_excluded() {
		TestWithTestNGGroupsAndSetup.setupWasCalled = false;

		config.setGroups("automated");
		config.setExcludedGroups("integration");
		TestResults results = runner.runTest(TestWithTestNGGroupsAndSetup.class.getName());

		assertFalse(TestWithTestNGGroupsAndSetup.setupWasCalled);
		assertEquals(1, size(results));
	}

	private class MyJUnitXMLReporter extends JUnitXMLReporter {
		@Override
		public void onTestStart(ITestResult result) {
			wasCalled = true;
		}
	}
}
