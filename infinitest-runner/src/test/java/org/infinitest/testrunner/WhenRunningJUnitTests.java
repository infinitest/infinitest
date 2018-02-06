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
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.jupiter.api.Assertions.*;

import junit.framework.AssertionFailedError;
import org.infinitest.*;
import org.infinitest.testrunner.exampletests.*;
import org.junit.*;

public class WhenRunningJUnitTests {
	private JUnit4Runner runner;
	private static final Class<?> TEST_CLASS = TestThatThrowsExceptionInConstructor.class;

	@Before
	public void inContext() {
		TestThatThrowsExceptionInConstructor.fail = true;
		FailingTest.fail = true;
		TestNGTest.fail = true;
		runner = new JUnit4Runner();
	}

	@After
	public void cleanup() {
		TestThatThrowsExceptionInConstructor.fail = false;
		FailingTest.fail = false;
	}

	@Test
	public void shouldFireNoEventsIfAllMethodsPass() {
		TestResults results = runner.runTest(PassingTestCase.class.getName());
		assertTrue(isEmpty(results));
	}

	@Test
	public void shouldFireEventsToReportFailingResults() {
		TestResults results = runner.runTest(FailingTest.class.getName());
		TestEvent expectedEvent = methodFailed("", FailingTest.class.getName(), "shouldFail", new AssertionError());
		assertEventsEquals(expectedEvent, getOnlyElement(results));
	}

	@Test
	public void shouldDetectFailureInBeforeMethod() {
		TestResults results = runner.runTest(FailingJUnit4TestWithBefore.class.getName());
		assertFalse(isEmpty(results));
	}

	@Test
	public void shouldDetectFailureInBeforeClassMethod() {
		TestResults results = runner.runTest(FailingJUnit4TestWithBeforeClass.class.getName());
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
		TestResults results = runner.runTest(MultiTest.class.getName());
		assertEquals(2, size(results.getMethodStats()));
		MethodStats methodStats = get(results.getMethodStats(), 0);
		assertTrue(methodStats.startTime <= methodStats.stopTime);
	}

	@Test(expected = MissingClassException.class)
	public void shouldThrowExceptionIfTestDoesNotExist() {
		runner.runTest("test");
	}

	@Test
	public void shouldSupportTestNG() {
		Iterable<TestEvent> events = runner.runTest(TestNGTest.class.getName());
		TestEvent expectedEvent = methodFailed(TestNGTest.class.getName(), "shouldFail", new AssertionError("expected [false] but found [true]"));
		assertEventsEquals(expectedEvent, getOnlyElement(events));
	}

	@Test
	public void shouldSupportJUnit5() {
		Iterable<TestEvent> events = runner.runTest(JUnit5Test.class.getName());
		TestEvent expectedEvent = methodFailed(JUnit5Test.class.getName(), "shouldFail", new AssertionFailedError("expected: <true> but was: <false>"));
		assertEventsEquals(expectedEvent, getOnlyElement(events));
	}

	@Test
	public void shouldDetectJUnit5Tests() {
		assertTrue(runner.isJUnit5Test(JUnit5Test.class));
		assertFalse(runner.isJUnit5Test(PassingTestCase.class));
	}

	private void assertEventsEquals(TestEvent expected, TestEvent actual) {
		assertThat(actual).isEqualTo(expected);
		assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
		assertThat(actual.getType()).isEqualTo(expected.getType());
		assertThat(actual.getErrorClassName()).isEqualTo(expected.getErrorClassName());
	}

	public void testCaseStarting(TestEvent event) {
		fail("Native runner should never fire this");
	}
}
