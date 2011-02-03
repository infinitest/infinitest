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
        TestNGTest.fail = true;
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
        TestEvent expectedEvent = methodFailed("", TEST_CLASS.getName(), "shouldPass", new IllegalStateException());
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

    @Test
    public void shouldSupportTestNG()
    {
        Iterable<TestEvent> events = runner.runTest(TestNGTest.class.getName());
        TestEvent expectedEvent = methodFailed(TestNGTest.class.getName(), "shouldFail", new AssertionError(
                        "expected:<false> but was:<true>"));
        assertEventsEquals(expectedEvent, getOnlyElement(events));
    }

    private void assertEventsEquals(TestEvent expected, TestEvent actual)
    {
        assertEquals("event", expected, actual);
        assertEquals("message", expected.getMessage(), actual.getMessage());
        assertEquals("type", expected.getType(), actual.getType());
        assertEquals("errorClassName", expected.getErrorClassName(), actual.getErrorClassName());
    }

    public void testCaseStarting(TestEvent event)
    {
        fail("Native runner should never fire this");
    }
}
