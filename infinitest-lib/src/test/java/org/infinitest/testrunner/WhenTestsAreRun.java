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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.*;

@SuppressWarnings("unused")
class WhenTestsAreRun extends AbstractRunnerTest implements TestResultsListener {
	private static final String TEST_SUCCEEDED = "Test Succeeded";
	private static final String FAILURE_MSG = "Test failed as expected";

	private AbstractTestRunner runner;
	private List<TestEvent> methodEvents;
	private List<TestEvent> startedEvents;
	private EventSupport support;

	@BeforeEach
	void inContext() throws IOException {
		runner = new InProcessRunner();
		methodEvents = new ArrayList<TestEvent>();
		startedEvents = new ArrayList<TestEvent>();
		runner.addTestResultsListener(this);
		support = new EventSupport();
		runner.addTestResultsListener(support);
		TestFakeProduct.setUpState();
		TestJUnit4TestCase.enable();
	}

	@AfterEach
	void cleanup() {
		runner = null;
		methodEvents = null;
		startedEvents = null;
		runner = null;
		TestFakeProduct.tearDownState();
		TestJUnit4TestCase.disable();
	}

	@Test
	void shouldStartAndFinishIgnoredTests() throws InterruptedException {
		runTests(IgnoredTest.class);
		support.assertTestsStarted(IgnoredTest.class);
		support.assertTestPassed(IgnoredTest.class);
	}

	@Test
	void shouldTreatIgnoredMethodsAsSuccesses() throws InterruptedException {
		runTests(TestWithIgnoredMethod.class);
		support.assertTestsStarted(TestWithIgnoredMethod.class);
		support.assertTestPassed(TestWithIgnoredMethod.class);
	}

	@SuppressWarnings("all")
	public static class TestWithIgnoredMethod {
		@org.junit.Test
		public void shouldPass() {
		}

		@Ignore
		@org.junit.Test
		public void shouldBeIgnored() {
			fail();
		}
	}

	@SuppressWarnings("all")
	@Ignore
	public static class IgnoredTest {
		@org.junit.Test
		public void shouldPass() {
		}

		@org.junit.Test
		public void shouldFail() {
			fail();
		}
	}

	public static class TestThatChecksForHelloWorldProperty {
		@org.junit.Test
		public void shouldFailIfPropertyNotSet() {
			assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
			assertNotNull(System.getProperty("hello.world"));
		}
	}

	@Test
	void exploreJUnit4TestAdapterBehavior() {
		JUnit4TestAdapter adapter = new JUnit4TestAdapter(TestJUnit4TestCase.class);
		assertEquals(2, adapter.countTestCases());
	}

	@Test
	void shouldHandleJUnit3TestsWithExceptionsInConstructor() throws InterruptedException {
		runTests(JUnit3TestWithExceptionInConstructor.class);
		support.assertTestFailed(JUnit3TestWithExceptionInConstructor.class.getName());

		TestEvent event = methodEvents.get(0);
		assertEquals(JUnit3TestWithExceptionInConstructor.class.getName(), event.getTestName());
		assertTrue(event.getMessage().startsWith("Exception in constructor: testThatPasses (java.lang.NullPointerException"));
	}

	@Test
	void shouldFireTestCaseCompleteWhenFinished() throws InterruptedException {
		runTests(TestJUnit4TestCase.class);
		support.assertTestRun(TestJUnit4TestCase.class);
	}

	@Test
	void shouldHandleAnnotatedTests() throws InterruptedException {
		runTests(TestJUnit4TestCase.class);
		assertEquals(methodEvents.toString(), 1, methodEvents.size());

		TestEvent testEvent = methodEvents.get(0);
		assertEquals("Test Case Name", TestJUnit4TestCase.class.getName(), testEvent.getTestName());
		assertEquals("shouldFailIfPropertyIsSet", testEvent.getTestMethod());
		assertEquals("Test Failed", testEvent.getMessage());
		assertTrue(testEvent.isFailure());
		assertEquals(testEvent.getErrorClassName() + " is not an AssertionFailedError", AssertionError.class.getSimpleName(), testEvent.getErrorClassName());
	}

	@Test
	void shouldFireEventWhenTestIsStarted() throws InterruptedException {
		runTests(TestJUnit4TestCase.class);
		assertEquals("One passing and one failing test", 1, startedEvents.size());
	}

	@Test
	void shouldFireEventsForFailingTestsOnly() throws IOException, InterruptedException {
		setTestSuccess("testNumber1", TEST_SUCCEEDED, true);
		runTests(TestFakeProduct.class);
		assertNotNull("Test was not set up", getCallCount("setUp"));
		assertNotNull("Test was not run", getCallCount("testRun"));
		assertNotNull("Test was not torn down", getCallCount("tearDown"));
		assertEquals("Event Size", 0, methodEvents.size());
		support.assertTestPassed(TestFakeProduct.class);
	}

	public void testCaseFinished(TestEvent event) {

	}

	public void testRunFailure() throws FileNotFoundException, IOException, InterruptedException {
		TestFakeProduct.setTestSuccess("testNumber1", FAILURE_MSG, false);
		runTests(TestFakeProduct.class);
		assertEquals("No event was fired", 2, methodEvents.size());

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
	void shouldReportTestsInErrorAsFailures() throws Exception {
		TestFakeProduct.setTestError("testNumber1", IllegalArgumentException.class);
		runTests(TestFakeProduct.class);
		assertEquals("Error event count", 1, methodEvents.size());

		TestEvent e = methodEvents.get(0);
		assertEquals("", e.getMessage());
		assertEquals(IllegalArgumentException.class.getSimpleName(), e.getErrorClassName());
		assertEquals("Test Case Name", TestFakeProduct.class.getName(), e.getTestName());
		assertEquals("testNumber1", e.getTestMethod());
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
