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
