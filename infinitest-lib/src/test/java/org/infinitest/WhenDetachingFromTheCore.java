package org.infinitest;

import static org.hamcrest.CoreMatchers.*;
import static org.infinitest.CoreDependencySupport.*;
import static org.junit.Assert.*;

import org.infinitest.changedetect.FakeChangeDetector;
import org.junit.Before;
import org.junit.Test;

public class WhenDetachingFromTheCore
{
    private int eventCount;
    private TestQueueListener listener;

    @Before
    public void inContext()
    {
        listener = new TestQueueAdapter()
        {
            @Override
            public void reloading()
            {
                eventCount++;
            }
        };
    }

    @Test
    public void shouldNoLongerRecieveEvents()
    {
        ControlledEventQueue eventQueue = new ControlledEventQueue();
        DefaultInfinitestCore core = createCore(new FakeChangeDetector(), withNoTestsToRun(), eventQueue);
        core.addTestQueueListener(listener);
        core.reload();
        eventQueue.flush();
        assertEquals(1, eventCount);

        core.removeTestQueueListener(listener);
        core.reload();
        eventQueue.flush();
        assertEquals(1, eventCount);
    }

    @Test
    public void shouldTreatNormalizedListenersAsEquivelent()
    {
        EventNormalizer normalizer = new EventNormalizer(new FakeEventQueue());
        assertEquals(normalizer.testQueueNormalizer(listener), normalizer.testQueueNormalizer(listener));
        assertThat(normalizer.testQueueNormalizer(listener),
                        not(equalTo(normalizer.testQueueNormalizer(new TestQueueAdapter()))));
    }
}
