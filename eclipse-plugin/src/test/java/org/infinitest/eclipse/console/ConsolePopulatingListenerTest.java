package org.infinitest.eclipse.console;

import static org.easymock.EasyMock.*;
import static org.infinitest.ConsoleOutputListener.OutputType.*;

import org.junit.Before;
import org.junit.Test;

public class ConsolePopulatingListenerTest
{
    private TextOutputWriter writer;
    private ConsolePopulatingListener listener;

    @Before
    public void inContext()
    {
        writer = createMock(TextOutputWriter.class);
        listener = new ConsolePopulatingListener(writer);
    }

    @Test
    public void shouldWriteTestConsoleOutputToTheEclipseConsole()
    {
        writer.appendText("some new text");
        replay(writer);
        listener.consoleOutputUpdate("some new text", STDOUT);
    }

    @Test
    public void shouldActivateTheConsoleWhenStdErrorOutputIsWritten()
    {
        writer.appendText("some new text");
        writer.activate();
        replay(writer);

        listener.consoleOutputUpdate("some new text", STDERR);
    }
}
