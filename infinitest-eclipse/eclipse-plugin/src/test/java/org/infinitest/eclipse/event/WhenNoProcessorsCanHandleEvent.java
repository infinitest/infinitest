package org.infinitest.eclipse.event;

import static org.easymock.EasyMock.*;

import org.infinitest.EventQueue;
import org.infinitest.eclipse.event.CoreUpdateNotifier;
import org.junit.Test;

public class WhenNoProcessorsCanHandleEvent
{
    @Test
    public void shouldDoNothing()
    {
        EventQueue eventQueue = createMock(EventQueue.class);
        replay(eventQueue);

        CoreUpdateNotifier factory = new CoreUpdateNotifier(eventQueue);
        factory.processEvent(null);
        verify(eventQueue);
    }
}
