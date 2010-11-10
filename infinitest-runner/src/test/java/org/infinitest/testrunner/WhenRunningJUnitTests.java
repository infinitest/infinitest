package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import org.infinitest.MissingClassException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenRunningJUnitTests
{
    private JUnit4Runner runner;
    private static final Class<?> TEST_CLASS = TestThatThrowsExceptionInConstructor.class;

    @Before
    public void inContext()
    {
        TestThatThrowsExceptionInConstructor.fail = true;
        FailingTest.fail = true;
        runner = new JUnit4Runner();
    }

    @After
    public void cleanup()
    {
        TestThatThrowsExceptionInConstructor.fail = false;
        FailingTest.fail = false;
    }

    @Test
    public void shouldFireNoEventsIfAllMethodsPass()
    {
        TestResults results = runner.runTest(PassingTestCase.class.getName());
        assertTrue(isEmpty(results));
    }

    @Test
    public void shouldFireEventsToReportFailingResults()
    {
        TestResults results = runner.runTest(FailingTest.class.getName());
        TestEvent expectedEvent = methodFailed("", FailingTest.class.getName(), "shouldFail", new AssertionError());
        assertEventsEquals(expectedEvent, getOnlyElement(results));
    }

    @Test
    public void shouldIgnoreSuiteMethods()
    {
        TestResults results = runner.runTest(JUnit3TestWithASuiteMethod.class.getName());
        assertTrue(isEmpty(results));
    }

    @Test
    public void shouldTreatUninstantiableTestsAsFailures()
    {
        Iterable<TestEvent> events = runner.runTest(TEST_CLASS.getName());
        TestEvent expectedEvent = methodFailed(null, TEST_CLASS.getName(), "shouldPass", new IllegalStateException());
        assertEventsEquals(expectedEvent, getOnlyElement(events));
    }

    @Test
    public void shouldIncludeTimingsForMethodRuns()
    {
        TestResults results = runner.runTest(MultiTest.class.getName());
        assertEquals(2, size(results.getMethodStats()));
        MethodStats methodStats = get(results.getMethodStats(), 0);
        assertTrue(methodStats.startTime <= methodStats.stopTime);
    }

    @Test(expected = MissingClassException.class)
    public void shouldThrowExceptionIfTestDoesNotExist()
    {
        runner.runTest("test");
    }

    private void assertEventsEquals(TestEvent expected, TestEvent actual)
    {
        assertEquals(expected, actual);
        assertEquals(expected.getMessage(), actual.getMessage());
        assertEquals(expected.getType(), actual.getType());
    }

    public void testCaseStarting(TestEvent event)
    {
        fail("Native runner should never fire this");
    }
}
