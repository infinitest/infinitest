/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.testrunner.queue;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.junit.*;

public class ProcessorRunnableTest {
	@After
	public void cleanup() {
		// Clear interrupted state
		Thread.interrupted();
	}

	@Test
	public void shouldNotAttemptReQueueIfNoTestHasBeenPulled() {
		final Collection<String> additions = newLinkedList();
		Queue<String> queue = new LinkedList<String>() {
			private static final long serialVersionUID = -1L;

			@Override
			public boolean add(String o) {
				return additions.add(o);
			}
		};
		QueueProcessor processor = mock(QueueProcessor.class);

		ProcessorRunnable runnable = new ProcessorRunnable(queue, processor, null, 1, mock(ConcurrencyController.class));
		Thread.currentThread().interrupt();
		runnable.run();
		assertTrue(additions.isEmpty());
		verify(processor).close();
	}

	@Test
	public void shouldReQueueTestIfEventDispatchFails() throws InterruptedException, IOException {
		Queue<String> testQueue = newLinkedList(asList("test1"));
		QueueProcessor processor = mock(QueueProcessor.class);
		doThrow(new QueueDispatchException(new Throwable())).when(processor).process("test1");

		ProcessorRunnable runnable = new ProcessorRunnable(testQueue, processor, null, 1, mock(ConcurrencyController.class));
		runnable.run();
		assertEquals("test1", getOnlyElement(testQueue));
	}
}
