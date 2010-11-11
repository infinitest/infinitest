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
import static org.infinitest.util.InfinitestUtils.*;

import java.util.Queue;

import org.infinitest.ConcurrencyController;
import org.infinitest.QueueDispatchException;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestRunAborted;
import org.infinitest.testrunner.RunnerEventSupport;

class ProcessorRunnable implements Runnable
{
    private final QueueProcessor processor;
    private final Queue<String> testQueue;
    private final RunnerEventSupport eventSupport;
    private final int initialSize;
    private final ConcurrencyController concurrencySemaphore;

    public ProcessorRunnable(Queue<String> testQueue, QueueProcessor processor, RunnerEventSupport eventSupport,
                    int initialSize, ConcurrencyController concurrencySemaphore)
    {
        this.testQueue = testQueue;
        this.processor = processor;
        this.eventSupport = eventSupport;
        this.initialSize = initialSize;
        this.concurrencySemaphore = concurrencySemaphore;
    }

    public void terminate()
    {
        processor.cleanup();
    }

    private void fireEvent()
    {
        eventSupport.fireQueueEvent(new TestQueueEvent(newArrayList(testQueue), initialSize));
    }

    public void run()
    {
        try
        {
            String currentTest = null;
            try
            {
                concurrencySemaphore.acquire();
                while (!testQueue.isEmpty())
                {
                    currentTest = testQueue.poll();
                    processor.process(currentTest);
                    // RISK There might be a race condition here.
                    // If we fire all the events for a test
                    // run, and then the run is terminated, it's possible the queue events would not
                    // be fired. but testRunComplete would have been fired already. Is this actually
                    // a problem? I have no idea.
                    fireEvent();
                }
            }
            catch (QueueDispatchException e)
            {
                reQueueTestAndTerminateProcess(currentTest);
            }
            catch (InterruptedException e)
            {
                reQueueTestAndTerminateProcess(currentTest);
            }
            catch (TestRunAborted e)
            {
                reQueueTest(currentTest);
                // The process is already dead, no need to clean up
                clearLingeringInterruptedState();
            }
            finally
            {
                concurrencySemaphore.release();
                processor.close();
            }
        }
        // CHECKSTYLE:OFF
        // Need to catch any errors here before they fall off into the thread pool ether.
        catch (Throwable e)
        // CHECKSTYLE:ON
        {
            log("Error occurred while processing test run", e);
        }
    }

    private void reQueueTestAndTerminateProcess(String currentTest)
    {
        reQueueTest(currentTest);
        processor.cleanup();
    }

    private void clearLingeringInterruptedState()
    {
        Thread.interrupted();
    }

    private void reQueueTest(String currentTest)
    {
        if (currentTest != null)
        {
            log(currentTest + " was interrupted. Re-running.");
            testQueue.add(currentTest);
        }
    }
}