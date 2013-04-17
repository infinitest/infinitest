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
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class QueueConsumerTest {
	private BlockingQueue<String> events;
	private QueueConsumer queue;
	private RunnerEventSupport runnerEvents;
	protected List<TestQueueEvent> queueUpdates;
	private Semaphore processSemaphore;

	@Before
	public void inContext() {
		processSemaphore = null;
		events = new SynchronousQueue<String>(true);
		runnerEvents = new RunnerEventSupport(this);
		queueUpdates = newArrayList();
		queue = new QueueConsumer(runnerEvents, new LinkedList<String>()) {
			@Override
			protected QueueProcessor createQueueProcessor() {
				return new FakeQueueProcessor();
			}
		};
		runnerEvents.addTestQueueListener(new TestQueueAdapter() {
			@Override
			public void testQueueUpdated(TestQueueEvent event) {
				queueUpdates.add(event);
			}
		});
	}

	@Test
	public void shouldSpawnWorkersWhenQueueIsPopulated() throws Exception {
		queue.push(asList("test1"));
		assertEquals("Starting test1", poll());
		assertEquals("Finished test1", poll());
		assertEquals("Closed", poll());
	}

	@Test
	public void shouldConsumeUntilQueueIsEmpty() throws Exception {
		queue.push(asList("test1", "test2"));
		assertEquals("Starting test1", poll());
		assertEquals("Finished test1", poll());
		assertEquals("Starting test2", poll());
		assertEquals("Finished test2", poll());
		assertEquals("Closed", poll());
		assertQueueEventsFired();
	}

	@Test
	public void shouldThrowProcessorCreationErrorsWhenPushIsCalled() {
		queue = new QueueConsumer(runnerEvents, new LinkedList<String>(), 50) {
			@Override
			protected QueueProcessor createQueueProcessor() {
				throw new RuntimeException("Could not create");
			}
		};

		try {
			queue.push(asList("test1"));
		} catch (RuntimeException e) {
			assertEquals("Could not create", e.getMessage());
		}
	}

	@Test
	public void shouldForceCleanIfInterruptFails() throws Exception {
		queue = new QueueConsumer(runnerEvents, new LinkedList<String>(), 50) {
			@Override
			protected QueueProcessor createQueueProcessor() {
				return new ProcessorThatHangsUntilCleaned();
			}
		};

		queue.push(asList("test1"));
		assertEquals("Starting test1", poll());

		queue.push(asList("test2"));
		assertEquals("Cleaned", poll());

		assertEquals("Starting test2", poll());
	}

	@Test
	public void shouldInterruptProcessorWhenAddingToQueue() throws Exception {
		processSemaphore = new Semaphore(1);
		processSemaphore.acquire();
		queue.push(asList("test1"));
		assertEquals("Starting test1", poll());

		queue.push(asList("test2"));
		assertEquals("Cleaned", poll());
		assertEquals("Closed", poll());
		assertEquals("Starting test2", poll());
		processSemaphore.release();
		assertEquals("Finished test2", poll());
		assertEquals("Starting test1", poll());
		assertEquals("Finished test1", poll());

		assertEquals("Closed", poll());
	}

	private void assertQueueEventsFired() {
		TestQueueEvent firstEvent = get(queueUpdates, 0);
		assertEquals(firstEvent.getTestQueue(), asList("test1", "test2"));
		assertEquals(firstEvent.getInitialSize(), 2);

		TestQueueEvent secondEvent = get(queueUpdates, 1);
		assertEquals(secondEvent.getTestQueue(), asList("test2"));
		assertEquals(secondEvent.getInitialSize(), 2);

		TestQueueEvent thirdEvent = get(queueUpdates, 2);
		assertEquals(thirdEvent.getTestQueue(), emptyList());
	}

	private String poll() throws InterruptedException {
		return events.poll(1000, MILLISECONDS);
	}

	private class ProcessorThatHangsUntilCleaned implements QueueProcessor {
		private boolean cleaned;

		public synchronized void cleanup() {
			cleaned = true;
			try {
				if (Thread.interrupted()) {
					events.put("Interrupted");
				}
				events.put("Cleaned");
				notify();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		public void close() {
			try {
				if (Thread.interrupted()) {
					events.put("Interrupted");
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		public void process(String test) throws InterruptedException {
			if (Thread.interrupted()) {
				events.put("Interrupted");
			}
			events.put("Starting " + test);
			synchronized (this) {
				while (!cleaned) {
					wait();
				}
			}
			throw new TestRunAborted(test, new NullPointerException());
		}
	}

	private class FakeQueueProcessor implements QueueProcessor {
		public void process(String test) throws InterruptedException {
			events.put("Starting " + test);
			if (processSemaphore != null) {
				processSemaphore.acquire();
			}
			events.put("Finished " + test);
			if (processSemaphore != null) {
				processSemaphore.release();
			}
		}

		public void close() {
			try {
				events.put("Closed");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		public void cleanup() {
			try {
				events.put("Cleaned");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
