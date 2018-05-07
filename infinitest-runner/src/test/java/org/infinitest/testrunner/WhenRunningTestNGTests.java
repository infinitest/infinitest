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

import org.infinitest.config.*;
import org.junit.*;
import org.testng.*;
import org.testng.reporters.*;

public class WhenRunningTestNGTests {
	private DefaultRunner runner;
	private static final String CLASS_UNDER_TEST = TestWithTestNGGroups.class.getName();

	@Before
	public void inContext() {
		runner = new DefaultRunner();
		TestWithTestNGGroups.fail = true;
		TestWithTestNGClassAnnotationOnly.fail = true;
		TestWithTestNGMixedLevelAnnotations.fail = true;
		TestWithTestNGGroups.dependencyFail = true;
		MockListener.wasCalled = false;
	}

	@After
	public void cleanup() {
		TestWithTestNGGroups.fail = false;
		TestWithTestNGClassAnnotationOnly.fail = false;
		TestWithTestNGMixedLevelAnnotations.fail = false;
		TestWithTestNGGroups.dependencyFail = false;
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
		runner.setTestConfigurationSource(withExcludedGroups("slow", "manual", "green"));
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(0, size(results));
	}

	@Test
	public void shouldExecuteOnlyTheSpecifiedGroup() {
		runner.setTestConfigurationSource(withGroups("slow"));
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(2, size(results));

		runner.setTestConfigurationSource(withGroups("shouldbetested"));
		results = runner.runTest(CLASS_UNDER_TEST);
		assertEquals(0, size(results));
	}

	/**
	 * if the group "slow" is included, but "mixed" excluded, a test with groups
	 * = "mixed", "slow" will not be executed
	 */
	@Test
	public void combineIncludedAndExcludedGroups() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("slow").excludedGroups("mixed").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(CLASS_UNDER_TEST);

		assertEquals(1, size(results));
	}

	@Test
	public void shouldReactToListener() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().testngListeners(MockListener.class.getName()).build();

		runner.setTestConfigurationSource(withConfig(configuration));

		runner.runTest(CLASS_UNDER_TEST);

		assertTrue(MockListener.wasCalled);
	}

	@Test
	public void should_call_setup_if_one_of_its_group_is_activated() {
		TestWithTestNGGroupsAndSetup.setupWasCalled = false;

		runner.setTestConfigurationSource(withGroups("automated"));

		TestResults results = runner.runTest(TestWithTestNGGroupsAndSetup.class.getName());

		assertTrue(TestWithTestNGGroupsAndSetup.setupWasCalled);
		assertEquals(1, size(results));
	}

	@Test
	public void should_NOT_call_setup_if_one_of_its_group_is_excluded() {
		TestWithTestNGGroupsAndSetup.setupWasCalled = false;

		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("automated").excludedGroups("integration").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(TestWithTestNGGroupsAndSetup.class.getName());

		assertFalse(TestWithTestNGGroupsAndSetup.setupWasCalled);
		assertEquals(1, size(results));
	}

	@Test
	public void should_run_tests_with_class_level_test_annotation_only() {
		TestResults results = runner.runTest(TestWithTestNGClassAnnotationOnly.class.getName());

		assertEquals(1, size(results));
		// distinguish AssertionError (on tests failure) and Exception (thrown
		// on initialization error)
		TestEvent collectedEvent = results.iterator().next();
		assertEquals(AssertionError.class.getName(), collectedEvent.getFullErrorClassName());
	}

	@Test
	public void should_execute_only_group_defined_in_class_level_test_annotation() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("manual").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(TestWithTestNGMixedLevelAnnotations.class.getName());

		assertEquals(2, size(results));
	}

	@Test
	public void should_execute_only_group_overridden_by_method_level_test_annotation() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("slow").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(TestWithTestNGMixedLevelAnnotations.class.getName());

		assertEquals(1, size(results));
	}

	public static class MockListener extends JUnitXMLReporter {
		static boolean wasCalled = false;

		@Override
		public void onTestStart(ITestResult result) {
			wasCalled = true;
		}
	}

	private MemoryInfinitestConfigurationSource withExcludedGroups(String... excludedGroups) {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().excludedGroups(excludedGroups).build();

		return withConfig(configuration);
	}

	private MemoryInfinitestConfigurationSource withGroups(String... groups) {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups(groups).build();

		return withConfig(configuration);
	}

	private MemoryInfinitestConfigurationSource withConfig(InfinitestConfiguration configuration) {
		return new MemoryInfinitestConfigurationSource(configuration);
	}
}
