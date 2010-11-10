package org.infinitest;

public interface ConsoleOutputListener
{
    public enum OutputType
    {
        STDOUT, STDERR
    }

    /**
     * Called when there is more text to append to the console output. Note that this may be a
     * single line, part of a line, or multiple lines.
     */
    void consoleOutputUpdate(String newText, OutputType outputType);
}
