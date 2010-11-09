package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.infinitest.TestQueueAdapter;
import org.infinitest.TestQueueEvent;
import org.junit.Before;
import org.junit.Test;

public class TestRunnerEventSupport
{
    private int events;
    private RunnerEventSupport eventSupport;

    @Before
    public void inContext()
    {
        eventSupport = new RunnerEventSupport(this);
    }

    @Test
    public void shouldFireEventsWhenTestRunFinishes()
    {
        eventSupport.addTestQueueListener(new TestQueueAdapter()
        {
            @Override
            public void testRunComplete()
            {
                events++;
            }
        });
        eventSupport.fireTestRunComplete();
        assertEquals(1, events);
    }

    @Test
    public void shouldFireTestQueueEvents()
    {
        final List<TestQueueEvent> queueEvents = newArrayList();
        eventSupport.addTestQueueListener(new TestQueueAdapter()
        {
            @Override
            public void testQueueUpdated(TestQueueEvent event)
            {
                queueEvents.add(event);
            }
        });
        TestQueueEvent event = new TestQueueEvent(asList("MyTest"), 1);
        eventSupport.fireQueueEvent(event);
        assertThat(queueEvents, hasItem(event));
    }
}
