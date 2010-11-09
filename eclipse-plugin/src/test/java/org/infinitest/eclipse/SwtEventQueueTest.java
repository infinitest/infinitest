package org.infinitest.eclipse;

import static java.lang.Thread.*;
import static java.util.concurrent.TimeUnit.*;
import static org.eclipse.core.runtime.jobs.Job.*;
import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.infinitest.QueueDispatchException;
import org.infinitest.ThreadSafeFlag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SwtEventQueueTest
{
    private SwtEventQueue queue;
    protected boolean failingRunnableExecuted;
    protected boolean started;

    @Before
    public void inContext()
    {
        queue = new SwtEventQueue();
    }

    @After
    public void cleanup()
    {
        // Clear interrupted state
        Thread.interrupted();
    }

    @Test
    public void shouldUseBuildPriorty()
    {
        assertEquals(BUILD, queue.createJob().getPriority());
    }

    @Test
    public void shouldExecuteEvents() throws Exception
    {
        final ThreadSafeFlag ran = new ThreadSafeFlag();
        queue.push(new Runnable()
        {
            public void run()
            {
                ran.trip();
            }
        });
        ran.assertTripped();
    }

    @Test(expected = QueueDispatchException.class)
    public void shouldThrowExceptionWhenPushIsInterrupted()
    {
        currentThread().interrupt();
        queue.push(new Runnable()
        {
            public void run()
            {
            }
        });
    }

    @Test
    public void shouldExecuteSuccessiveEvents() throws Exception
    {
        final Semaphore semaphore = new Semaphore(3);
        semaphore.acquire(3);
        Runnable semaphoreReleasingTask = new Runnable()
        {
            public void run()
            {
                semaphore.release();
            }
        };
        queue.push(semaphoreReleasingTask);
        queue.push(semaphoreReleasingTask);
        queue.push(semaphoreReleasingTask);
        assertTrue(semaphore.tryAcquire(1000, MILLISECONDS));
    }

    @Test
    public void shouldLogFailingEvents() throws Exception
    {
        final ThreadSafeFlag flag = new ThreadSafeFlag();
        queue.push(new Runnable()
        {
            public void run()
            {
                failingRunnableExecuted = true;
                throw new RuntimeException();
            }
        });
        queue.push(new Runnable()
        {
            public void run()
            {
                flag.trip();
            }
        });

        flag.assertTripped();
        assertTrue(failingRunnableExecuted);
    }
}
