package org.infinitest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Comparator;

import org.infinitest.testrunner.TestResultsListener;
import org.infinitest.testrunner.TestRunner;
import org.junit.Test;

public class WhenATestIsRun
{
    @Test
    public void shouldEvent()
    {
        EventNormalizer normalizer = new EventNormalizer(new ControlledEventQueue());
        assertNotNull(normalizer.consoleEventNormalizer(new ConsoleListenerAdapter()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFireEventsForConsoleUpdates()
    {
        TestRunner runner = createMock(TestRunner.class);
        runner.addTestResultsListener((TestResultsListener) anyObject());
        runner.setTestPriority((Comparator<String>) anyObject());
        runner.addConsoleOutputListener((ConsoleOutputListener) anyObject());
        runner.removeConsoleOutputListener((ConsoleOutputListener) anyObject());

        replay(runner);
        DefaultInfinitestCore core = new DefaultInfinitestCore(runner, new ControlledEventQueue());
        ConsoleListenerAdapter listener = new ConsoleListenerAdapter();
        core.addConsoleOutputListener(listener);
        core.removeConsoleOutputListener(listener);
        verify(runner);
    }
}
