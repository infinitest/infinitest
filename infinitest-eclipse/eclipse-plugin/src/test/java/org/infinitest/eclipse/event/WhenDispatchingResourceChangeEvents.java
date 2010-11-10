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
