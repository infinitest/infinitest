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
package org.infinitest;

import static com.google.common.collect.Iterables.*;
import static java.util.Arrays.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class WhenTestsResultsAreProcessed extends ResultCollectorTestSupport
{
    private EventSupport statusListener;

    @Before
    public void inContext()
    {
        statusListener = new EventSupport();
    }

    @Test
    public void canAttachToCore()
    {
        InfinitestCore core = mock(InfinitestCore.class);

        new ResultCollector(core);

        verify(core).addTestQueueListener(any(TestQueueListener.class));
        verify(core).addTestResultsListener(any(ResultCollector.class));
        verify(core).addDisabledTestListener(any(DisabledTestListener.class));
    }

    @Test
    public void shouldListenToCoreForResults()
    {
        assertEquals(SCANNING, collector.getStatus());
    }

    @Test
    public void shouldChangeStatusToRunning()
    {
        collector.addStatusChangeListener(statusListener);
        collector.testQueueUpdated(new TestQueueEvent(asList("aTest"), 1));
        collector.testQueueUpdated(new TestQueueEvent(emptyStringList(), 1));
        statusListener.assertStatesChanged(RUNNING);

        collector.testRunComplete();
        statusListener.assertStatesChanged(RUNNING, PASSING);
        assertEquals(PASSING, collector.getStatus());
    }

    @Test
    public void shouldNotifyTestCollectorOfTestEvents()
    {
        FailureListenerSupport listener = new FailureListenerSupport();
        collector.addChangeListener(listener);
        TestEvent failureEvent = methodFailed("message", DEFAULT_TEST_NAME, "methodName", new AssertionError());

        testRun(failureEvent);
        assertEquals(failureEvent, getOnlyElement(collector.getFailures()));
        assertEquals(failureEvent, getOnlyElement(listener.added));
        assertTrue(listener.removed.isEmpty());

        testRunWith("testName2");
        assertTrue(listener.removed.isEmpty());

        listener.added.clear();
        testRun();
        // collector.testCaseFinished(testCaseFinished("testName"));
        assertEquals(failureEvent, getOnlyElement(listener.removed));
        assertTrue(listener.added.isEmpty());
    }

    @Test
    public void shouldIgnoreDisabledTestsThatArentFailing()
    {
        collector.testsDisabled(asList("NotAFailingTest"));
    }

    @Test
    public void shouldClearFailuresForDisabledTests()
    {
        String testName = "MyClass";
        testRunWith(testName, methodFailed(testName, "someMethod", new NullPointerException()));
        assertEquals(1, collector.getFailures().size());

        final Collection<TestEvent> removed = Lists.newArrayList();
        collector.addChangeListener(new FailureListListener()
        {
            public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
            {
                removed.addAll(failuresRemoved);
            }

            public void failuresUpdated(Collection<TestEvent> updatedFailures)
            {
                throw new UnsupportedOperationException();
            }
        });
        collector.testsDisabled(asList(testName));
        assertEquals(0, collector.getFailures().size());
        assertEquals("MyClass", getOnlyElement(removed).getTestName());
    }
}
