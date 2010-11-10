package org.infinitest.testrunner;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.IOException;
import java.io.InputStream;

import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.testrunner.process.ProcessConnection;
import org.infinitest.testrunner.process.ProcessConnectionFactory;
import org.infinitest.testrunner.queue.QueueProcessor;

class TestQueueProcessor implements QueueProcessor
{
    private final ProcessConnectionFactory factory;
    private final RunnerEventSupport eventSupport;

    private final ProcessConnection currentConnection;

    public TestQueueProcessor(RunnerEventSupport eventSupport, ProcessConnectionFactory factory,
                    RuntimeEnvironment environment) throws IOException
    {
        this.eventSupport = eventSupport;
        this.factory = factory;
        currentConnection = establishConnection(environment);
    }

    public void process(String testName)
    {
        getEventSupport().fireStartingEvent(testName);
        TestResults results = currentConnection.runTest(testName);
        getEventSupport().fireTestCaseComplete(testName, results);
    }

    public void close()
    {
        getEventSupport().fireTestRunComplete();
        currentConnection.close();
    }

    private ProcessConnection establishConnection(RuntimeEnvironment environment) throws IOException
    {
        return factory.getConnection(environment, new OutputStreamHandler()
        {
            public void processStream(InputStream stream, OutputType type)
            {
                new Thread(new ConsoleOutputProcessor(stream, type, getEventSupport())).start();
            }
        });
    }

    private RunnerEventSupport getEventSupport()
    {
        return eventSupport;
    }

    public void cleanup()
    {
        if (!currentConnection.abort())
        {
            log(WARNING, "Failed to clean up after terminated test run");
        }
        else
        {
            log("Test runner process terminated");
        }
    }
}
