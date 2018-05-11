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

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyIterable;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.testrunner.TestResultTestUtils.assertEventsEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.infinitest.MissingClassException;
import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.MemoryInfinitestConfigurationSource;
import org.infinitest.testrunner.exampletests.junit3.JUnit3TestWithASuiteMethod;
import org.infinitest.testrunner.exampletests.junit4.JUnit4FailingTest;
import org.infinitest.testrunner.exampletests.junit4.JUnit4FailingTestWithBefore;
import org.infinitest.testrunner.exampletests.junit4.JUnit4FailingTestWithBeforeClass;
import org.infinitest.testrunner.exampletests.junit4.Junit4FailingTestsWithCategories;
import org.infinitest.testrunner.exampletests.junit4.Junit4FailingTestsWithCategories.IgnoreMe;
import org.infinitest.testrunner.exampletests.junit4.Junit4FailingTestsWithCategories.IgnoreMeToo;
import org.infinitest.testrunner.exampletests.junit4.Junit4FailingTestsWithCategories.UsuallyRunMe;
import org.infinitest.testrunner.exampletests.junit4.Junit4MultiTest;
import org.infinitest.testrunner.exampletests.junit4.Junit4PassingTestCase;
import org.infinitest.testrunner.exampletests.junit4.Junit4TestThatThrowsExceptionInConstructor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JUnit4RunnerTest {
	private static final String[] EMPTY = new String[0];
	private DefaultRunner runner;
	private static final Class<?> TEST_CLASS = Junit4TestThatThrowsExceptionInConstructor.class;

	@Before
	public void inContext() {
		Junit4TestThatThrowsExceptionInConstructor.fail = true;
		JUnit4FailingTest.fail = true;
		runner = new DefaultRunner();
	}

	@After
	public void cleanup() {
		Junit4TestThatThrowsExceptionInConstructor.fail = false;
		JUnit4FailingTest.fail = false;
	}

	@Test
	public void shouldFireNoEventsIfAllMethodsPass() {
		TestResults results = runner.runTest(Junit4PassingTestCase.class.getName());
		assertTrue(isEmpty(results));
	}

	@Test
	public void shouldFireEventsToReportFailingResults() {
		TestResults results = runner.runTest(JUnit4FailingTest.class.getName());
		TestEvent expectedEvent = methodFailed("", JUnit4FailingTest.class.getName(), "shouldFail", new AssertionError());
		assertEventsEquals(expectedEvent, getOnlyElement(results));
	}

	@Test
	public void shouldIgnoreSuiteMethods() {
		TestResults results = runner.runTest(JUnit3TestWithASuiteMethod.class.getName());
		assertTrue(isEmpty(results));
	}

	@Test
	public void shouldDetectFailureInBeforeMethod() {
		TestResults results = runner.runTest(JUnit4FailingTestWithBefore.class.getName());
		assertFalse(isEmpty(results));
	}

	@Test
	public void shouldDetectFailureInBeforeClassMethod() {
		TestResults results = runner.runTest(JUnit4FailingTestWithBeforeClass.class.getName());
		assertFalse(isEmpty(results));
	}

	@Test
	public void shouldTreatUninstantiableTestsAsFailures() {
		Iterable<TestEvent> events = runner.runTest(TEST_CLASS.getName());
		TestEvent expectedEvent = methodFailed(null, TEST_CLASS.getName(), "shouldPass", new IllegalStateException());
		assertEventsEquals(expectedEvent, getOnlyElement(events));
	}

	@Test
	public void shouldIncludeTimingsForMethodRuns() {
		TestResults results = runner.runTest(Junit4MultiTest.class.getName());
		assertEquals(2, size(results.getMethodStats()));
		MethodStats methodStats = get(results.getMethodStats(), 0);
		assertTrue(methodStats.startTime <= methodStats.stopTime);
	}

	@Test(expected = MissingClassException.class)
	public void shouldThrowExceptionIfTestDoesNotExist() {
		runner.runTest("test");
	}


	@Test
	public void shouldExecuteAllTestsIfNoExcludedCategories() {
		runner.setTestConfigurationSource(withExcludedGroups(EMPTY));

		TestResults results = runner.runTest(Junit4FailingTestsWithCategories.class.getName());

		assertThat(size(results), is(3));
	}

	@Test
	public void shouldNotExecuteTestInOneExcludedCategory() {
		runner.setTestConfigurationSource(withExcludedGroups(IgnoreMe.class.getName()));

		TestResults results = runner.runTest(Junit4FailingTestsWithCategories.class.getName());

		assertThat(size(results), is(2));
	}

	@Test
	public void shouldNotExecuteTestInAnyExcludedCategories() {
		runner.setTestConfigurationSource(withExcludedGroups(IgnoreMe.class.getName(), IgnoreMeToo.class.getName()));

		TestResults results = runner.runTest(Junit4FailingTestsWithCategories.class.getName());

		assertThat(size(results), is(1));
	}

	@Test
	public void shouldNotRunAnyTestsIfAllAreExcluded() throws Exception {
		String[] allCategories = { IgnoreMe.class.getName(), IgnoreMeToo.class.getName(), UsuallyRunMe.class.getName() };
		runner.setTestConfigurationSource(withExcludedGroups(allCategories));

		TestResults results = runner.runTest(Junit4FailingTestsWithCategories.class.getName());

		assertThat(results, is(emptyIterable()));
	}

	


	@Test
	public void shouldIgnoreNonPublicJUnit4Tests() {
		final String testClass = "org.infinitest.testrunner.exampletests.junit4.JUnit4NonPublicTest";
		final Iterable<TestEvent> events = runner.runTest(testClass);
		final TestEvent expectedEvent =
				methodFailed(
						testClass,
						"initializationError",
						new Exception("The class " + testClass + " is not public."));
		TestResultTestUtils.assertEventsEquals(expectedEvent, getFirst(events, null));
	}

	public void testCaseStarting(TestEvent event) {
		fail("Native runner should never fire this");
	}

	private MemoryInfinitestConfigurationSource withExcludedGroups(String... excludedGroups) {
		InfinitestConfiguration configuration = InfinitestConfiguration.builder().excludedGroups(excludedGroups).build();

		return new MemoryInfinitestConfigurationSource(configuration);
	}
}
