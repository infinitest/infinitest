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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.core.runtime.jobs.Job.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.*;

import org.infinitest.*;
import org.infinitest.eclipse.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwtEventQueueTest {
	private SwtEventQueue queue;
	protected boolean failingRunnableExecuted;
	protected boolean started;

	@BeforeEach
	void inContext() {
		queue = new SwtEventQueue();
	}

	@AfterEach
	void cleanup() {
		// Clear interrupted state
		Thread.interrupted();
	}

	@Test
	void shouldUseBuildPriorty() {
		assertEquals(BUILD, queue.createJob().getPriority());
	}

	@Test
	void shouldExecuteEvents() throws Exception {
		final ThreadSafeFlag ran = new ThreadSafeFlag();
		queue.push(new Runnable() {
			@Override
			public void run() {
				ran.trip();
			}
		});
		ran.assertTripped();
	}

	@Test
	void shouldThrowExceptionWhenPushIsInterrupted() {
		currentThread().interrupt();
		
		assertThatThrownBy(() -> queue.push(() -> {})).isInstanceOf(QueueDispatchException.class);
	}

	@Test
	void shouldExecuteSuccessiveEvents() throws Exception {
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
	void shouldLogFailingEvents() throws Exception {
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
