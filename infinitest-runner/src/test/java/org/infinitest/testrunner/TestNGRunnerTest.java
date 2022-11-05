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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.testrunner.TestResultTestUtils.assertEventsEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.MemoryInfinitestConfigurationSource;
import org.infinitest.testrunner.exampletests.testng.TestNGTest;
import org.infinitest.testrunner.exampletests.testng.TestWithTestNGClassAnnotationOnly;
import org.infinitest.testrunner.exampletests.testng.TestWithTestNGGroups;
import org.infinitest.testrunner.exampletests.testng.TestWithTestNGGroupsAndSetup;
import org.infinitest.testrunner.exampletests.testng.TestWithTestNGMixedLevelAnnotations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.ITestResult;
import org.testng.reporters.JUnitXMLReporter;

class TestNGRunnerTest {
	private DefaultRunner runner;
	private static final String CLASS_UNDER_TEST = TestWithTestNGGroups.class.getName();

	@BeforeEach
	void inContext() {
		runner = new DefaultRunner();
		TestNGTest.fail = true;
		TestWithTestNGGroups.fail = true;
		TestWithTestNGClassAnnotationOnly.fail = true;
		TestWithTestNGMixedLevelAnnotations.fail = true;
		TestWithTestNGGroups.dependencyFail = true;
		MockListener.wasCalled = false;
	}

	@AfterEach
	void cleanup() {
		TestNGTest.fail = false;
		TestWithTestNGGroups.fail = false;
		TestWithTestNGClassAnnotationOnly.fail = false;
		TestWithTestNGMixedLevelAnnotations.fail = false;
		TestWithTestNGGroups.dependencyFail = false;
	}
	

	@Test
	void shouldSupportTestNG() {
		Iterable<TestEvent> events = runner.runTest(TestNGTest.class.getName());
		TestEvent expectedEvent = methodFailed(TestNGTest.class.getName(), "shouldFail", new AssertionError("expected [false] but found [true]"));
		assertEventsEquals(expectedEvent, getOnlyElement(events));
	}

	/**
	 * no test filters set: bad tests fail. But: the dependent test
	 * "shouldNoBeTestedDueToDependencyOnFilteredGroup" is not executed
	 */
	@Test
	void shouldFailIfBadTestsAreNotFiltered() {
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
	void shouldExecuteDependentTestIfMasterGroupWorked() {
		TestWithTestNGGroups.fail = false;
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		TestEvent testEvent = getOnlyElement(results);
		assertEquals("shouldNoBeTestedDueToDependencyOnFilteredGroup", testEvent.getTestMethod());
		assertEquals(AssertionError.class.getName(), testEvent.getFullErrorClassName());
	}

	@Test
	void shouldNotFailWithFilteredGroupsSet() {
		runner.setTestConfigurationSource(withExcludedGroups("slow", "manual", "green"));
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertThat(results).isEmpty();
	}

	@Test
	void shouldExecuteOnlyTheSpecifiedGroup() {
		runner.setTestConfigurationSource(withGroups("slow"));
		TestResults results = runner.runTest(CLASS_UNDER_TEST);
		assertThat(results).hasSize(2);

		runner.setTestConfigurationSource(withGroups("shouldbetested"));
		results = runner.runTest(CLASS_UNDER_TEST);
		assertThat(results).isEmpty();
	}

	/**
	 * if the group "slow" is included, but "mixed" excluded, a test with groups
	 * = "mixed", "slow" will not be executed
	 */
	@Test
	void combineIncludedAndExcludedGroups() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("slow").excludedGroups("mixed").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(CLASS_UNDER_TEST);

		assertThat(results).hasSize(1);
	}

	@Test
	void shouldReactToListener() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().testngListeners(MockListener.class.getName()).build();

		runner.setTestConfigurationSource(withConfig(configuration));

		runner.runTest(CLASS_UNDER_TEST);

		assertTrue(MockListener.wasCalled);
	}

	@Test
	void should_call_setup_if_one_of_its_group_is_activated() {
		TestWithTestNGGroupsAndSetup.setupWasCalled = false;

		runner.setTestConfigurationSource(withGroups("automated"));

		TestResults results = runner.runTest(TestWithTestNGGroupsAndSetup.class.getName());

		assertTrue(TestWithTestNGGroupsAndSetup.setupWasCalled);
		assertThat(results).hasSize(1);
	}

	@Test
	void should_NOT_call_setup_if_one_of_its_group_is_excluded() {
		TestWithTestNGGroupsAndSetup.setupWasCalled = false;

		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("automated").excludedGroups("integration").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(TestWithTestNGGroupsAndSetup.class.getName());

		assertFalse(TestWithTestNGGroupsAndSetup.setupWasCalled);
		assertThat(results).hasSize(1);
	}

	@Test
	void should_run_tests_with_class_level_test_annotation_only() {
		TestResults results = runner.runTest(TestWithTestNGClassAnnotationOnly.class.getName());

		assertThat(results).hasSize(1);
		// distinguish AssertionError (on tests failure) and Exception (thrown
		// on initialization error)
		TestEvent collectedEvent = results.iterator().next();
		assertEquals(AssertionError.class.getName(), collectedEvent.getFullErrorClassName());
	}

	@Test
	void should_execute_only_group_defined_in_class_level_test_annotation() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("manual").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(TestWithTestNGMixedLevelAnnotations.class.getName());

		assertThat(results).hasSize(2);
	}

	@Test
	void should_execute_only_group_overridden_by_method_level_test_annotation() {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().includedGroups("slow").build();

		runner.setTestConfigurationSource(withConfig(configuration));

		TestResults results = runner.runTest(TestWithTestNGMixedLevelAnnotations.class.getName());

		assertThat(results).hasSize(1);
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
