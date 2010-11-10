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
