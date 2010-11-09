package org.infinitest.eclipse.console;

import org.infinitest.ConsoleOutputListener;

public class ConsolePopulatingListener implements ConsoleOutputListener
{
    private final TextOutputWriter writer;

    public ConsolePopulatingListener(TextOutputWriter writer)
    {
        this.writer = writer;
    }

    public void consoleOutputUpdate(String newText, OutputType outputType)
    {
        writer.appendText(newText);
    }
}
