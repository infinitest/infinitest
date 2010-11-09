package org.infinitest.eclipse.console;

import org.infinitest.ReloadListener;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestQueueListener;

public class ConsoleClearingListener implements TestQueueListener, ReloadListener
{
    private final TextOutputWriter writer;

    public ConsoleClearingListener(TextOutputWriter writer)
    {
        this.writer = writer;
    }

    public void reloading()
    {
        writer.clearConsole();
    }

    public void testQueueUpdated(TestQueueEvent event)
    {
        if (event.getInitialSize() == event.getTestQueue().size())
            writer.clearConsole();
    }

    public void testRunComplete()
    {
        // nothing to do here
    }
}
