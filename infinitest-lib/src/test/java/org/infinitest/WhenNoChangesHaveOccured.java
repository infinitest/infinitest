package org.infinitest;

import static java.util.Arrays.*;
import static org.infinitest.CoreStatus.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenNoChangesHaveOccured
{
    @Test
    public void shouldStillUpdateStatus()
    {
        ResultCollector collector = new ResultCollector();
        collector.testRunComplete();
        assertEquals(PASSING, collector.getStatus());
    }

    @Test
    public void shouldStillSetRunningStatus()
    {
        EventSupport eventAssert = new EventSupport();
        ResultCollector collector = new ResultCollector();
        collector.addStatusChangeListener(eventAssert);
        collector.testQueueUpdated(new TestQueueEvent(asList("MyTest"), 1));
        collector.testRunComplete();
        collector.testQueueUpdated(new TestQueueEvent(asList("MyTest"), 1));
        collector.testRunComplete();
        assertEquals(PASSING, collector.getStatus());
        eventAssert.assertStatesChanged(RUNNING, PASSING, RUNNING, PASSING);
    }
}
