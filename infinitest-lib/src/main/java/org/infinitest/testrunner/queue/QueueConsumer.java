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

import static com.google.common.collect.Lists.*;
import static java.util.concurrent.Executors.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;

public abstract class QueueConsumer {
	private final Queue<String> testQueue;
	private final RunnerEventSupport eventSupport;
	private QueueProcessorThread processorThread;
	private final long testTimeout;
	private final ExecutorService executor;
	private ConcurrencyController semaphore;

	public QueueConsumer(RunnerEventSupport eventSupport, Queue<String> testQueue) {
		this(eventSupport, testQueue, 2000);
	}

	public QueueConsumer(RunnerEventSupport eventSupport, Queue<String> testQueue, long testTimeout) {
		this.eventSupport = eventSupport;
		this.testQueue = testQueue;
		this.testTimeout = testTimeout;
		executor = newSingleThreadExecutor();
		semaphore = new SingleLockConcurrencyController();
	}

	public void push(List<String> tests) {
		testQueue.addAll(tests);
		startProcessing();
	}

	private void startProcessing() {
		try {
			QueueProcessor processor = createQueueProcessor();
			ProcessorRunnable runnable = new ProcessorRunnable(testQueue, processor, eventSupport, testQueue.size(), semaphore);
			executor.execute(new ProcessingKickoffRunnable(runnable));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private class ProcessingKickoffRunnable implements Runnable {
		private final ProcessorRunnable runnable;

		private ProcessingKickoffRunnable(ProcessorRunnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				if (testsAreRunning()) {
					stopCurrentRun();
				}
				startNewTestRun();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void startNewTestRun() {
			processorThread = new QueueProcessorThread(runnable);
			eventSupport.fireQueueEvent(new TestQueueEvent(newArrayList(testQueue), testQueue.size()));
			processorThread.start();
		}

		private boolean testsAreRunning() {
			return (processorThread != null) && processorThread.isAlive();
		}
	}

	private void stopCurrentRun() throws InterruptedException {
		// Die hard
		processorThread.interrupt();
		processorThread.join(testTimeout);
		if (processorThread.isAlive()) {
			// Die Harder
			processorThread.terminate();
			processorThread.join(5000);
			if (processorThread.isAlive()) {
				throw new IllegalStateException();
			}
		}
	}

	protected abstract QueueProcessor createQueueProcessor() throws IOException;

	public void setConcurrencySemaphore(ConcurrencyController controller) {
		semaphore = controller;
	}
}
