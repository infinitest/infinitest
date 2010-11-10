package org.infinitest;

import static org.easymock.EasyMock.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

public class WhenWatchingMultipleCores
{
    private ResultCollector collector;
    private InfinitestCore core;

    @Before
    public void inContext()
    {
        collector = new ResultCollector();
        core = createMock(InfinitestCore.class);
    }

    @Test
    public void canAttachAndDetachFromCores()
    {
        core.addTestQueueListener(isA(TestQueueListener.class));
        core.addTestResultsListener(collector);
        core.addDisabledTestListener(collector);
        core.removeTestQueueListener(isA(TestQueueListener.class));
        core.removeTestResultsListener(collector);
        core.removeDisabledTestListener(collector);
        replay(core);

        collector.attachCore(core);
        collector.detachCore(core);
        verify(core);
    }

    @Test
    public void shouldRemoveFailuresForACoreWhenItIsDetached()
    {
        TestEvent event = withFailingMethod("method1");
        TestCaseEvent caseEvent = new TestCaseEvent(TEST_NAME, this, new TestResults(event));
        core.removeTestQueueListener(null);
        core.removeTestResultsListener(collector);
        core.removeDisabledTestListener(collector);
        expect(core.isEventSourceFor(caseEvent)).andReturn(true);
        replay(core);
        collector.testCaseComplete(caseEvent);
        assertTrue(collector.hasFailures());

        collector.detachCore(core);
        assertFalse(collector.hasFailures());
    }
}
