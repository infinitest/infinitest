package org.infinitest.testrunner;

import static org.infinitest.util.InfinitestUtils.*;

import java.io.IOException;
import java.io.InputStream;

import org.infinitest.ConsoleOutputListener.OutputType;

public class ConsoleOutputProcessor implements Runnable
{
    private final RunnerEventSupport eventSupport;
    private final InputStream outputSource;
    private final OutputType outputType;

    public ConsoleOutputProcessor(InputStream stream, OutputType outputType, RunnerEventSupport eventSupport)
    {
        this.outputSource = stream;
        this.outputType = outputType;
        this.eventSupport = eventSupport;
    }

    public void run()
    {
        try
        {
            int bytesRead;
            byte[] buffer = new byte[1024 * 10];
            while ((bytesRead = outputSource.read(buffer)) != -1)
            {
                eventSupport.fireConsoleUpdateEvent(new String(buffer, 0, bytesRead), outputType);
            }
            outputSource.close();
        }
        catch (IOException e)
        {
            log("Error while reading console output from the test runner process", e);
            throw new RuntimeException(e);
        }
    }
}
