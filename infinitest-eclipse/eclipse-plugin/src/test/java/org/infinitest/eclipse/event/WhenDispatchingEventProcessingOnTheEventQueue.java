package org.infinitest.eclipse.event;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.junit.Test;

public class WhenDispatchingEventProcessingOnTheEventQueue
{
    @Test
    public void shouldInvokeTheProcessorFromARunnable()
    {
        MockProcessor processor = new MockProcessor();
        IResourceChangeEvent event = createNiceMock(IResourceChangeEvent.class);
        replay(event);
        EventProcessorRunnable runnable = new EventProcessorRunnable(processor, event);
        runnable.run();
        assertEquals(event, processor.getEvent());
    }
}
