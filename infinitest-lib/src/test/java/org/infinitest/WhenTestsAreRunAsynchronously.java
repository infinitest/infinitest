package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;

import org.junit.Before;
import org.junit.Test;

import com.fakeco.fakeproduct.TestJUnit4TestCase;

public class WhenTestsAreRunAsynchronously
{
    private DefaultInfinitestCore core;
    private EventSupport eventSupport;

    @Before
    public void inContext()
    {
        core = createAsyncCore(withChangedFiles(), withTests(FAILING_TEST, PASSING_TEST, TestJUnit4TestCase.class));
        eventSupport = new EventSupport(5000);
        core.addTestQueueListener(eventSupport);
        core.addTestResultsListener(eventSupport);
        // Not sure why, but maven fails if we don't reset the interrupted state here!
        Thread.interrupted();
    }

    @Test
    public void canUpdateWhileTestsAreRunning() throws Exception
    {
        core.update();
        eventSupport.assertQueueChanges(1);
        // There are three tests in the queue, we're updating before they're all finished
        core.update();

        eventSupport.assertQueueChanges(3);
        eventSupport.assertRunComplete();
    }
}
