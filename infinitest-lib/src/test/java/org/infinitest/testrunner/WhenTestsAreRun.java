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

import static com.fakeco.fakeproduct.TestFakeProduct.*;
import static junit.framework.Assert.*;
import static org.junit.Assume.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.infinitest.EventSupport;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fakeco.fakeproduct.JUnit3TestWithExceptionInConstructor;
import com.fakeco.fakeproduct.TestFakeProduct;
import com.fakeco.fakeproduct.TestJUnit4TestCase;
import com.google.common.base.Strings;

@SuppressWarnings("unused")
public class WhenTestsAreRun extends AbstractRunnerTest implements TestResultsListener
{
    private static final String TEST_SUCCEEDED = "Test Succeeded";
    private static final String FAILURE_MSG = "Test failed as expected";

    private AbstractTestRunner runner;
    private List<TestEvent> methodEvents;
    private List<TestEvent> startedEvents;
    private EventSupport support;

    @Before
    public void inContext() throws IOException
    {
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
    public void cleanup()
    {
        runner = null;
        methodEvents = null;
        startedEvents = null;
        runner = null;
        TestFakeProduct.tearDownState();
        TestJUnit4TestCase.disable();
    }

    @Test
    public void shouldStartAndFinishIgnoredTests() throws InterruptedException
    {
        runTests(IgnoredTest.class);
        support.assertTestsStarted(IgnoredTest.class);
        support.assertTestPassed(IgnoredTest.class);
    }

    @Test
    public void shouldTreatIgnoredMethodsAsSuccesses() throws InterruptedException
    {
        runTests(TestWithIgnoredMethod.class);
        support.assertTestsStarted(TestWithIgnoredMethod.class);
        support.assertTestPassed(TestWithIgnoredMethod.class);
    }

    @SuppressWarnings("all")
    public static class TestWithIgnoredMethod
    {
        @Test
        public void shouldPass()
        {
        }

        @Ignore
        @Test
        public void shouldBeIgnored()
        {
            fail();
        }
    }

    @SuppressWarnings("all")
    @Ignore
    public static class IgnoredTest
    {
        @Test
        public void shouldPass()
        {
        }

        @Test
        public void shouldFail()
        {
            fail();
        }
    }

    public static class TestThatChecksForHelloWorldProperty
    {
        @Test
        public void shouldFailIfPropertyNotSet()
        {
            assumeTrue(InfinitestTestUtils.testIsBeingRunFromInfinitest());
            assertNotNull(System.getProperty("hello.world"));
        }
    }

    @Test
    public void exploreJUnit4TestAdapterBehavior()
    {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(TestJUnit4TestCase.class);
        assertEquals(2, adapter.countTestCases());
    }

    @Test
    public void shouldHandleJUnit3TestsWithExceptionsInConstructor() throws InterruptedException
    {
        runTests(JUnit3TestWithExceptionInConstructor.class);
        support.assertTestFailed(JUnit3TestWithExceptionInConstructor.class.getName());

        TestEvent event = methodEvents.get(0);
        assertEquals(JUnit3TestWithExceptionInConstructor.class.getName(), event.getTestName());
        assertTrue(event.getMessage().startsWith(
                        "Exception in constructor: testThatPasses (java.lang.NullPointerException"));
    }

    @Test
    public void shouldFireTestCaseCompleteWhenFinished() throws InterruptedException
    {
        runTests(TestJUnit4TestCase.class);
        support.assertTestRun(TestJUnit4TestCase.class);
    }

    @Test
    public void shouldHandleAnnotatedTests() throws InterruptedException
    {
        runTests(TestJUnit4TestCase.class);
        assertEquals(methodEvents.toString(), 1, methodEvents.size());

        TestEvent testEvent = methodEvents.get(0);
        assertEquals("Test Case Name", TestJUnit4TestCase.class.getName(), testEvent.getTestName());
        assertEquals("shouldFailIfPropertyIsSet", testEvent.getTestMethod());
        assertEquals("Test Failed", testEvent.getMessage());
        assertTrue(testEvent.isFailure());
        assertTrue(testEvent.getErrorClassName() + " is not an AssertionFailedError", testEvent.getErrorClassName()
                        .equals(AssertionError.class.getSimpleName()));
    }

    @Test
    public void shouldFireEventWhenTestIsStarted() throws InterruptedException
    {
        runTests(TestJUnit4TestCase.class);
        assertEquals("One passing and one failing test", 1, startedEvents.size());
    }

    @Test
    public void shouldFireEventsForFailingTestsOnly() throws IOException, InterruptedException
    {
        setTestSuccess("testNumber1", TEST_SUCCEEDED, true);
        runTests(TestFakeProduct.class);
        assertNotNull("Test was not set up", getCallCount("setUp"));
        assertNotNull("Test was not run", getCallCount("testRun"));
        assertNotNull("Test was not torn down", getCallCount("tearDown"));
        assertEquals("Event Size", 0, methodEvents.size());
        support.assertTestPassed(TestFakeProduct.class);
    }

    public void testCaseFinished(TestEvent event)
    {

    }

    public void testRunFailure() throws FileNotFoundException, IOException, InterruptedException
    {
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
    public void shouldReportTestsInErrorAsFailures() throws Exception
    {
        TestFakeProduct.setTestError("testNumber1", IllegalArgumentException.class);
        runTests(TestFakeProduct.class);
        assertEquals("Error event count", 1, methodEvents.size());

        TestEvent e = methodEvents.get(0);
        assertTrue(Strings.isNullOrEmpty(e.getMessage()));
        assertTrue(e.getErrorClassName().equals(IllegalArgumentException.class.getSimpleName()));
        assertEquals("Test Case Name", TestFakeProduct.class.getName(), e.getTestName());
        assertEquals("testNumber1", e.getTestMethod());
    }

    public void testCaseStarting(TestEvent event)
    {
        startedEvents.add(event);
    }

    @Override
    protected AbstractTestRunner getRunner()
    {
        return runner;
    }

    @Override
    protected void waitForCompletion()
    {
        // This runner is synchronous
    }

    public void testCaseComplete(TestCaseEvent event)
    {
        for (TestEvent each : event.getFailureEvents())
        {
            methodEvents.add(each);
        }
    }
}
