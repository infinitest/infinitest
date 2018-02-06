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

import static com.fakeco.fakeproduct.TestFakeProduct.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;
import org.junit.Test;

import com.fakeco.fakeproduct.*;

@SuppressWarnings("unused")
public class WhenTestsAreRun extends AbstractRunnerTest implements TestResultsListener {
	private static final String TEST_SUCCEEDED = "Test Succeeded";
	private static final String FAILURE_MSG = "Test failed as expected";

	private AbstractTestRunner runner;
	private List<TestEvent> methodEvents;
	private List<TestEvent> startedEvents;
	private EventSupport support;

	@Before
	public void inContext() throws IOException {
		runner = new InProcessRunner();
		methodEvents = new ArrayList<TestEvent>();
		startedEvents = new ArrayList<TestEvent>();
		runner.addTestResultsListener(this);
		support = new EventSupport();
		runner.addTestResultsListener(support);
		TestFakeProduct.setUpState();
		TestJUnit4TestCase.enable();
	}

	@After
	public void cleanup() {
		runner = null;
		methodEvents = null;
		startedEvents = null;
		runner = null;
		TestFakeProduct.tearDownState();
		TestJUnit4TestCase.disable();
	}

	@Test
	public void shouldStartAndFinishIgnoredTests() throws InterruptedException {
		runTests(IgnoredTest.class);
		support.assertTestsStarted(IgnoredTest.class);
		support.assertTestPassed(IgnoredTest.class);
	}

	@Test
	public void shouldTreatIgnoredMethodsAsSuccesses() throws InterruptedException {
		runTests(TestWithIgnoredMethod.class);
		support.assertTestsStarted(TestWithIgnoredMethod.class);
		support.assertTestPassed(TestWithIgnoredMethod.class);
	}

	@SuppressWarnings("all")
	public static class TestWithIgnoredMethod {
		@Test
		public void shouldPass() {
		}

		@Ignore
		@Test
		public void shouldBeIgnored() {
			fail();
		}
	}

	@SuppressWarnings("all")
	@Ignore
	public static class IgnoredTest {
		@Test
		public void shouldPass() {
		}

		@Test
		public void shouldFail() {
			fail();
		}
	}

	public static class TestThatChecksForHelloWorldProperty {
		@Test
		public void shouldFailIfPropertyNotSet() {
			assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
			assertNotNull(System.getProperty("hello.world"));
		}
	}

	@Test
	public void shouldFireTestCaseCompleteWhenFinished() throws InterruptedException {
		runTests(TestJUnit4TestCase.class);
		support.assertTestRun(TestJUnit4TestCase.class);
	}

	@Test
	public void shouldHandleAnnotatedTests() throws InterruptedException {
		runTests(TestJUnit4TestCase.class);
		assertEquals(1, methodEvents.size(), methodEvents.toString());

		TestEvent testEvent = methodEvents.get(0);
		assertThat(testEvent.getTestName()).as("Test Case Name").isEqualTo(TestJUnit4TestCase.class.getName());
		assertThat(testEvent.getTestMethod()).isEqualTo("shouldFailIfPropertyIsSet");
		assertThat(testEvent.getMessage()).isEqualTo("Test Failed");
		assertThat(testEvent.isFailure()).isTrue();
		assertThat(testEvent.getErrorClassName())
				.as(testEvent.getErrorClassName() + " is not an AssertionError")
				.isEqualTo(AssertionError.class.getSimpleName());
	}

	@Test
	public void shouldFireEventWhenTestIsStarted() throws InterruptedException {
		runTests(TestJUnit4TestCase.class);
		assertEquals(1, startedEvents.size(), "One passing and one failing test");
	}

	@Test
	public void shouldFireEventsForFailingTestsOnly() throws IOException, InterruptedException {
		setTestSuccess("testNumber1", TEST_SUCCEEDED, true);
		runTests(TestFakeProduct.class);
		assertThat(getCallCount("setUp")).as("Test was not set up").isNotZero();
		assertThat(getCallCount("testNumber1")).as("Test 1 was not run").isNotZero();
		assertThat(getCallCount("testNumber2")).as("Test 2 was not run").isNotZero();
		assertThat(getCallCount("tearDown")).as("Test was not torn down").isNotZero();
		assertThat(methodEvents).as("Event Size").isEmpty();
		support.assertTestPassed(TestFakeProduct.class);
	}

	public void testCaseFinished(TestEvent event) {

	}

	public void testRunFailure() throws FileNotFoundException, IOException, InterruptedException {
		TestFakeProduct.setTestSuccess("testNumber1", FAILURE_MSG, false);
		runTests(TestFakeProduct.class);
		assertEquals(2, methodEvents.size(), "No event was fired");

		TestEvent e = methodEvents.get(0);
		assertEquals(FAILURE_MSG, e.getMessage());
		assertEquals(TestFakeProduct.class.getName(), e.getTestName());
		assertEquals("testNumber1", e.getTestMethod());
		assertNotNull("No exception supplied for failure", e.getErrorClassName());

		e = methodEvents.get(1);
		assertEquals(TEST_SUCCEEDED, e.getMessage());
		assertEquals(TestFakeProduct.class.getName(), e.getTestName());
		assertEquals("testNumber2", e.getTestMethod());
	}

	@Test
	public void shouldReportTestsInErrorAsFailures() throws Exception {
		TestFakeProduct.setTestError("testNumber1", IllegalArgumentException.class);
		runTests(TestFakeProduct.class);
		assertThat(methodEvents).as("Error event count").hasSize(1);

		TestEvent e = methodEvents.get(0);
		assertThat(e.getMessage()).isBlank();
		assertTrue(e.getErrorClassName().equals(IllegalArgumentException.class.getSimpleName()));
		assertThat(e.getTestName()).as("Test Case Name").isEqualTo(TestFakeProduct.class.getName());
		assertThat(e.getTestMethod()).isEqualTo("testNumber1");
	}

	@Override
	public void testCaseStarting(TestEvent event) {
		startedEvents.add(event);
	}

	@Override
	protected AbstractTestRunner getRunner() {
		return runner;
	}

	@Override
	protected void waitForCompletion() {
		// This runner is synchronous
	}

	@Override
	public void testCaseComplete(TestCaseEvent event) {
		for (TestEvent each : event.getFailureEvents()) {
			methodEvents.add(each);
		}
	}
}
