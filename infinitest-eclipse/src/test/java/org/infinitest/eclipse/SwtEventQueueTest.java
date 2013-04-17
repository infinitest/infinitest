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
package org.infinitest.eclipse;

import static java.lang.Thread.*;
import static java.util.concurrent.TimeUnit.*;
import static org.eclipse.core.runtime.jobs.Job.*;
import static org.junit.Assert.*;

import java.util.concurrent.*;

import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;

public class SwtEventQueueTest {
	private SwtEventQueue queue;
	protected boolean failingRunnableExecuted;
	protected boolean started;

	@Before
	public void inContext() {
		queue = new SwtEventQueue();
	}

	@After
	public void cleanup() {
		// Clear interrupted state
		Thread.interrupted();
	}

	@Test
	public void shouldUseBuildPriorty() {
		assertEquals(BUILD, queue.createJob().getPriority());
	}

	@Test
	public void shouldExecuteEvents() throws Exception {
		final ThreadSafeFlag ran = new ThreadSafeFlag();
		queue.push(new Runnable() {
			@Override
			public void run() {
				ran.trip();
			}
		});
		ran.assertTripped();
	}

	@Test(expected = QueueDispatchException.class)
	public void shouldThrowExceptionWhenPushIsInterrupted() {
		currentThread().interrupt();
		queue.push(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	@Test
	public void shouldExecuteSuccessiveEvents() throws Exception {
		final Semaphore semaphore = new Semaphore(3);
		semaphore.acquire(3);
		Runnable semaphoreReleasingTask = new Runnable() {
			@Override
			public void run() {
				semaphore.release();
			}
		};
		queue.push(semaphoreReleasingTask);
		queue.push(semaphoreReleasingTask);
		queue.push(semaphoreReleasingTask);
		assertTrue(semaphore.tryAcquire(1000, MILLISECONDS));
	}

	@Test
	public void shouldLogFailingEvents() throws Exception {
		final ThreadSafeFlag flag = new ThreadSafeFlag();
		queue.push(new Runnable() {
			@Override
			public void run() {
				failingRunnableExecuted = true;
				throw new RuntimeException();
			}
		});
		queue.push(new Runnable() {
			@Override
			public void run() {
				flag.trip();
			}
		});

		flag.assertTripped();
		assertTrue(failingRunnableExecuted);
	}
}
