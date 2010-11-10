package org.infinitest.testrunner;

import static org.apache.commons.io.IOUtils.*;
import static org.infinitest.ConsoleOutputListener.OutputType.*;
import static org.junit.Assert.*;

import org.infinitest.ConsoleOutputListener;
import org.junit.Test;

public class ConsoleOutputProcessorTest
{
    @Test
    public void shouldFireEventsToPublishConsoleOutput()
    {
        RunnerEventSupport eventSupport = new RunnerEventSupport(this);
        final StringBuffer updatedText = new StringBuffer();
        eventSupport.addConsoleOutputListener(new ConsoleOutputListener()
        {
            public void consoleOutputUpdate(String newText, OutputType outputType)
            {
                updatedText.append(newText);
            }
        });
        new ConsoleOutputProcessor(toInputStream("hello"), STDERR, eventSupport).run();
        assertEquals("hello", updatedText.toString());
    }
}
