package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenATestPasses
{
    @Test
    public void shouldFireSuccessEvents()
    {
        ControlledEventQueue eventQueue = new ControlledEventQueue();
        DefaultInfinitestCore core = createCore(withChangedFiles(), withTests(PASSING_TEST), eventQueue);
        ResultCollector collector = new ResultCollector(core);
        EventSupport testStatus = new EventSupport();
        core.addTestResultsListener(testStatus);

        core.update();
        eventQueue.flush();

        testStatus.assertTestsStarted(PASSING_TEST);
        testStatus.assertTestPassed(PASSING_TEST);
        assertFalse(collector.hasFailures());
    }
}
