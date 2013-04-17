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
import static org.infinitest.util.InfinitestUtils.*;

import java.util.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;

class ProcessorRunnable implements Runnable {
	private final QueueProcessor processor;
	private final Queue<String> testQueue;
	private final RunnerEventSupport eventSupport;
	private final int initialSize;
	private final ConcurrencyController concurrencySemaphore;

	public ProcessorRunnable(Queue<String> testQueue, QueueProcessor processor, RunnerEventSupport eventSupport, int initialSize, ConcurrencyController concurrencySemaphore) {
		this.testQueue = testQueue;
		this.processor = processor;
		this.eventSupport = eventSupport;
		this.initialSize = initialSize;
		this.concurrencySemaphore = concurrencySemaphore;
	}

	public void terminate() {
		processor.cleanup();
	}

	private void fireEvent() {
		eventSupport.fireQueueEvent(new TestQueueEvent(newArrayList(testQueue), initialSize));
	}

	@Override
	public void run() {
		try {
			String currentTest = null;
			try {
				concurrencySemaphore.acquire();
				while (!testQueue.isEmpty()) {
					currentTest = testQueue.poll();
					processor.process(currentTest);
					// RISK There might be a race condition here.
					// If we fire all the events for a test
					// run, and then the run is terminated, it's possible the
					// queue events would not
					// be fired. but testRunComplete would have been fired
					// already. Is this actually
					// a problem? I have no idea.
					fireEvent();
				}
			} catch (QueueDispatchException e) {
				reQueueTestAndTerminateProcess(currentTest);
			} catch (InterruptedException e) {
				reQueueTestAndTerminateProcess(currentTest);
			} catch (TestRunAborted e) {
				reQueueTest(currentTest);
				// The process is already dead, no need to clean up
				clearLingeringInterruptedState();
			} finally {
				concurrencySemaphore.release();
				processor.close();
			}
		}
		// CHECKSTYLE:OFF
		// Need to catch any errors here before they fall off into the thread
		// pool ether.
		catch (Throwable e)
		// CHECKSTYLE:ON
		{
			log("Error occurred while processing test run", e);
		}
	}

	private void reQueueTestAndTerminateProcess(String currentTest) {
		reQueueTest(currentTest);
		processor.cleanup();
	}

	private void clearLingeringInterruptedState() {
		Thread.interrupted();
	}

	private void reQueueTest(String currentTest) {
		if (currentTest != null) {
			log(currentTest + " was interrupted. Re-running.");
			testQueue.add(currentTest);
		}
	}
}