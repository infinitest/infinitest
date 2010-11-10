package org.infinitest.testrunner;

import java.io.InputStream;

import org.infinitest.ConsoleOutputListener.OutputType;

public interface OutputStreamHandler
{
    void processStream(InputStream stream, OutputType type);
}
