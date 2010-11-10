package org.infinitest.plugin;

import static org.easymock.EasyMock.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import org.infinitest.ConcurrencyController;
import org.infinitest.EventSupport;
import org.infinitest.FakeEventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.ResultCollector;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fakeco.fakeproduct.simple.FailingTest;
import com.fakeco.fakeproduct.simple.PassingTest;

public class WhenRunningTests
{
    private static EventSupport eventHistory;
    private static ResultCollector collector;

    @BeforeClass
    public static void inContext() throws InterruptedException
    {
        if (eventHistory == null)
        {
            InfinitestCoreBuilder builder = new InfinitestCoreBuilder(fakeEnvironment(), new FakeEventQueue());
            builder.setUpdateSemaphore(createMock(ConcurrencyController.class));
            builder.setFilter(new InfinitestTestFilter());
            InfinitestCore core = builder.createCore();
            collector = new ResultCollector(core);

            eventHistory = new EventSupport();
            core.addTestResultsListener(eventHistory);
            core.addTestQueueListener(eventHistory);

            core.update();
            eventHistory.assertRunComplete();
        }
    }

    @Test
    public void canListenForResultEvents()
    {
        eventHistory.assertTestFailed(FailingTest.class);
        eventHistory.assertTestPassed(PassingTest.class);
    }

    // This will frequently hang when something goes horribly wrong in the test runner
    @Test(timeout = 5000)
    public void canListenForChangesToTheTestQueue() throws Exception
    {
        eventHistory.assertQueueChanges(3);
    }

    @Test
    public void canCollectStateUsingAResultCollector()
    {
        assertEquals(1, collector.getFailures().size());
        assertEquals(FAILING, collector.getStatus());
    }
}