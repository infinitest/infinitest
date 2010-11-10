package org.infinitest.eclipse.event;

import static org.easymock.EasyMock.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.infinitest.EventQueue;
import org.infinitest.NamedRunnable;
import org.infinitest.eclipse.event.CoreUpdateNotifier;
import org.junit.Before;
import org.junit.Test;

public class WhenRespondingToResourceChangeEvents
{
    private EventQueue queue;

    private CoreUpdateNotifier chain;

    @Before
    public void inContext()
    {
        queue = createMock(EventQueue.class);
        chain = new CoreUpdateNotifier(queue);
        chain.addProcessor(new MockProcessor());
    }

    @Test
    public void shouldProcessEventsOnEventQueue()
    {
        queue.pushNamed((NamedRunnable) anyObject());
        IResourceChangeEvent event = createMock(IResourceChangeEvent.class);
        replay(queue, event);

        chain.processEvent(event);

        verify(queue);
    }
}
