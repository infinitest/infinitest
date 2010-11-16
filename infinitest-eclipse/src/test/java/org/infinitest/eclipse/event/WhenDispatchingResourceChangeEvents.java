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
package org.infinitest.eclipse.event;

import static org.easymock.EasyMock.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.infinitest.EventQueue;
import org.junit.Before;
import org.junit.Test;

public class WhenDispatchingResourceChangeEvents
{
    private CoreUpdateNotifier notifier;
    private EventQueue eventQueue;

    @Before
    public void inContext()
    {
        eventQueue = createMock(EventQueue.class);
        notifier = new CoreUpdateNotifier(eventQueue);
    }

    @Test
    public void shouldIgnoreUnknownEvents()
    {
        IResourceChangeEvent event = createMock(IResourceChangeEvent.class);
        expect(event.getDelta()).andReturn(createNiceMock(IResourceDelta.class));
        replay(eventQueue, event);

        notifier.resourceChanged(event);
        verify(eventQueue);
    }

    @Test
    public void shouldIgnoreEventsWithNoDelta()
    {
        replay(eventQueue);

        notifier.resourceChanged(createMock(IResourceChangeEvent.class));
        verify(eventQueue);
    }
}
