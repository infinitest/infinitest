package org.infinitest.testrunner;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

import org.infinitest.ConcurrencyController;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.testrunner.process.NativeConnectionFactory;
import org.infinitest.testrunner.process.ProcessConnectionFactory;
import org.infinitest.testrunner.queue.QueueConsumer;
import org.infinitest.testrunner.queue.QueueProcessor;
import org.infinitest.testrunner.queue.TestQueue;

public class MultiProcessRunner extends AbstractTestRunner
{
    private QueueConsumer queueConsumer;

    // DEBT Move into QueueConsumer.
    private Queue<String> queue;

    public MultiProcessRunner()
    {
        this(new NativeConnectionFactory(JUnit4Runner.class), null);
    }

    public MultiProcessRunner(final ProcessConnectionFactory remoteProcessManager, RuntimeEnvironment environment)
    {
        queue = new TestQueue(getTestPriority());

        setRuntimeEnvironment(environment);
        queueConsumer = new QueueConsumer(getEventSupport(), queue)
        {
            @Override
            protected QueueProcessor createQueueProcessor() throws IOException
            {
                return new TestQueueProcessor(getEventSupport(), remoteProcessManager, getRuntimeEnvironment());
            }
        };
    }

    @Override
    public void setConcurrencyController(ConcurrencyController semaphore)
    {
        super.setConcurrencyController(semaphore);
        queueConsumer.setConcurrencySemaphore(getConcurrencySemaphore());
    }

    @Override
    public void runTests(List<String> testNames)
    {
        if (!testNames.isEmpty())
        {
            queueConsumer.push(testNames);
        }
    }
}
