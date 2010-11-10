package org.infinitest.testrunner.process;

import java.io.InputStream;

import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.testrunner.OutputStreamHandler;

@SuppressWarnings("all")
public class NoOpOutputHandler implements OutputStreamHandler
{
    public void processStream(InputStream stream, OutputType type)
    {
    }
}
