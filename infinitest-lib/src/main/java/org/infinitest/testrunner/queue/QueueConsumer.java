/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.testrunner.queue;

import static com.google.common.collect.Lists.*;
import static java.util.concurrent.Executors.*;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import org.infinitest.ConcurrencyController;
import org.infinitest.SingleLockConcurrencyController;
import org.infinitest.TestQueueEvent;
import org.infinitest.testrunner.RunnerEventSupport;

public abstract class QueueConsumer
{
    private final Queue<String> testQueue;
    private final RunnerEventSupport eventSupport;
    private QueueProcessorThread processorThread;
    private final long testTimeout;
    private final ExecutorService executor;
    private ConcurrencyController semaphore;

    public QueueConsumer(RunnerEventSupport eventSupport, Queue<String> testQueue)
    {
        this(eventSupport, testQueue, 2000);
    }

    public QueueConsumer(RunnerEventSupport eventSupport, Queue<String> testQueue, long testTimeout)
    {
        this.eventSupport = eventSupport;
        this.testQueue = testQueue;
        this.testTimeout = testTimeout;
        this.executor = newSingleThreadExecutor();
        this.semaphore = new SingleLockConcurrencyController();
    }

    public void push(List<String> tests)
    {
        testQueue.addAll(tests);
        startProcessing();
    }

    private void startProcessing()
    {
        try
        {
            QueueProcessor processor = createQueueProcessor();
            ProcessorRunnable runnable = new ProcessorRunnable(testQueue, processor, eventSupport, testQueue.size(),
                            semaphore);
            executor.execute(new ProcessingKickoffRunnable(runnable));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private class ProcessingKickoffRunnable implements Runnable
    {
        private final ProcessorRunnable runnable;

        private ProcessingKickoffRunnable(ProcessorRunnable runnable)
        {
            this.runnable = runnable;
        }

        public void run()
        {
            try
            {
                if (testsAreRunning())
                {
                    stopCurrentRun();
                }
                startNewTestRun();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        private void startNewTestRun()
        {
            processorThread = new QueueProcessorThread(runnable);
            eventSupport.fireQueueEvent(new TestQueueEvent(newArrayList(testQueue), testQueue.size()));
            processorThread.start();
        }

        private boolean testsAreRunning()
        {
            return processorThread != null && processorThread.isAlive();
        }
    }

    private void stopCurrentRun() throws InterruptedException
    {
        // Die hard
        processorThread.interrupt();
        processorThread.join(testTimeout);
        if (processorThread.isAlive())
        {
            // Die Harder
            processorThread.terminate();
            processorThread.join(5000);
            if (processorThread.isAlive())
            {
                throw new IllegalStateException();
            }
        }
    }

    protected abstract QueueProcessor createQueueProcessor() throws IOException;

    public void setConcurrencySemaphore(ConcurrencyController controller)
    {
        this.semaphore = controller;
    }
}
