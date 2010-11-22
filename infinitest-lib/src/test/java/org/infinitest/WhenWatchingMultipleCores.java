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

import static org.easymock.EasyMock.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import junit.framework.AssertionFailedError;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

public class WhenWatchingMultipleCores
{
    private static final String TEST_NAME = "com.fakeco.TestFoo";

    private ResultCollector collector;
    private InfinitestCore core;

    @Before
    public void inContext()
    {
        collector = new ResultCollector();
        core = createMock(InfinitestCore.class);
    }

    @Test
    public void canAttachAndDetachFromCores()
    {
        core.addTestQueueListener(isA(TestQueueListener.class));
        core.addTestResultsListener(collector);
        core.addDisabledTestListener(collector);
        core.removeTestQueueListener(isA(TestQueueListener.class));
        core.removeTestResultsListener(collector);
        core.removeDisabledTestListener(collector);
        replay(core);

        collector.attachCore(core);
        collector.detachCore(core);
        verify(core);
    }

    @Test
    public void shouldRemoveFailuresForACoreWhenItIsDetached()
    {
        TestEvent event = withFailingMethod("method1");
        TestCaseEvent caseEvent = new TestCaseEvent(TEST_NAME, this, new TestResults(event));
        core.removeTestQueueListener(null);
        core.removeTestResultsListener(collector);
        core.removeDisabledTestListener(collector);
        expect(core.isEventSourceFor(caseEvent)).andReturn(true);
        replay(core);
        collector.testCaseComplete(caseEvent);
        assertTrue(collector.hasFailures());

        collector.detachCore(core);
        assertFalse(collector.hasFailures());
    }

    private static TestEvent withFailingMethod(String methodName)
    {
        return new TestEvent(METHOD_FAILURE, "", "", "", new AssertionFailedError());
    }
}
