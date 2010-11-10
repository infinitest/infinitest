package org.infinitest.eclipse.console;

import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;

import org.infinitest.TestQueueEvent;
import org.junit.Before;
import org.junit.Test;

public class ConsoleClearingListenerTest
{
    private TextOutputWriter writer;
    private ConsoleClearingListener listener;

    @Before
    public void inContext()
    {
        writer = createMock(TextOutputWriter.class);
        listener = new ConsoleClearingListener(writer);
    }

    @Test
    public void shouldClearConsoleWhenTestRunIsStarted()
    {
        writer.clearConsole();
        replay(writer);

        listener.testQueueUpdated(new TestQueueEvent(asList("test"), 1));
        verify(writer);
    }

    @Test
    public void shouldNotClearTheConsoleWhenTestsAreRunning()
    {
        replay(writer);

        listener.testQueueUpdated(new TestQueueEvent(asList("test"), 2));
        verify(writer);
    }

    @Test
    public void shouldClearTheConsoleWhenTheCoreIsReloaded()
    {
        writer.clearConsole();
        replay(writer);

        listener.reloading();
        verify(writer);
    }
}
