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
package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static org.easymock.EasyMock.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.util.List;

import org.infinitest.InfinitestCore;
import org.infinitest.eclipse.AggregateResultsListener;
import org.infinitest.eclipse.TestResultAggregator;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

public class WhenAggregatingTestEventsFromMultipleCores
{
    private TestResultAggregator aggregator;
    private InfinitestCore core;
    private List<Object> events;

    @Before
    public void inContext()
    {
        events = newArrayList();
        aggregator = new TestResultAggregator();
        core = createMock(InfinitestCore.class);
    }

    @Test
    public void shouldAttachToNewCores()
    {
        core.addTestResultsListener(aggregator);
        replay(core);

        aggregator.coreCreated(core);
        verify(core);
    }

    @Test
    public void shouldDetachFromRemovedCores()
    {
        core.removeTestResultsListener(aggregator);
        replay(core);

        aggregator.coreRemoved(core);
        verify(core);
    }

    @Test
    public void shouldNotifyListenersOfAggregatedEvents()
    {
        aggregator.addListeners(new AggregateResultsListener()
        {
            public void testCaseStarting(TestEvent event)
            {
                events.add(event);
            }

            public void testCaseComplete(TestCaseEvent event)
            {
                events.add(event);
            }
        });

        TestEvent startingEvent = testCaseStarting("Test1");
        aggregator.testCaseStarting(startingEvent);
        assertSame(startingEvent, getOnlyElement(events));

        TestCaseEvent completeEvent = new TestCaseEvent("Test1", this, new TestResults());
        aggregator.testCaseComplete(completeEvent);
        assertSame(completeEvent, get(events, 1));
    }
}
