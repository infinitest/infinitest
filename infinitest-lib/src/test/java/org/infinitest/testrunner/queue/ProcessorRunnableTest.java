package org.infinitest.testrunner.queue;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.infinitest.ConcurrencyController;
import org.infinitest.QueueDispatchException;
import org.junit.After;
import org.junit.Test;

public class ProcessorRunnableTest
{
    @After
    public void cleanup()
    {
        // Clear interrupted state
        Thread.interrupted();
    }

    @Test
    public void shouldNotAttemptReQueueIfNoTestHasBeenPulled()
    {
        final Collection<String> additions = newLinkedList();
        Queue<String> queue = new LinkedList<String>()
        {
            private static final long serialVersionUID = -1L;

            @Override
            public boolean add(String o)
            {
                return additions.add(o);
            }
        };
        QueueProcessor processor = createMock(QueueProcessor.class);
        processor.close();
        replay(processor);

        ProcessorRunnable runnable = new ProcessorRunnable(queue, processor, null, 1,
                        createNiceMock(ConcurrencyController.class));
        Thread.currentThread().interrupt();
        runnable.run();
        assertTrue(additions.isEmpty());
        verify(processor);
    }

    @Test
    public void shouldReQueueTestIfEventDispatchFails() throws InterruptedException, IOException
    {
        Queue<String> testQueue = newLinkedList("test1");
        QueueProcessor processor = createMock(QueueProcessor.class);
        processor.process("test1");
        expectLastCall().andThrow(new QueueDispatchException(new Throwable()));
        replay(processor);

        ProcessorRunnable runnable = new ProcessorRunnable(testQueue, processor, null, 1,
                        createMock(ConcurrencyController.class));
        runnable.run();
        verify(processor);
        assertEquals("test1", getOnlyElement(testQueue));
    }
}
