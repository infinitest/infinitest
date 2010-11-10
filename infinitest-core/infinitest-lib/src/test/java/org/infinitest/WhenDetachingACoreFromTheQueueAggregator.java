package org.infinitest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenDetachingACoreFromTheQueueAggregator
{
    @Test
    public void shouldRemoveFromMap()
    {
        QueueAggregator aggregator = new QueueAggregator();
        InfinitestCore core = createNiceMock(InfinitestCore.class);
        replay(core);
        aggregator.attach(core);
        aggregator.detach(core);
        assertTrue(aggregator.getCoreQueueListeners().isEmpty());
    }
}
