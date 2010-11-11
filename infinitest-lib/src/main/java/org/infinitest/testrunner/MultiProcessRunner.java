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
