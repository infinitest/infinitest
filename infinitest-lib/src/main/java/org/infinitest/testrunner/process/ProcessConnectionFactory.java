package org.infinitest.testrunner.process;

import java.io.IOException;

import org.infinitest.RuntimeEnvironment;
import org.infinitest.testrunner.OutputStreamHandler;

public interface ProcessConnectionFactory
{
    ProcessConnection getConnection(RuntimeEnvironment environment, OutputStreamHandler consoleOutputHandler)
                    throws IOException;
}