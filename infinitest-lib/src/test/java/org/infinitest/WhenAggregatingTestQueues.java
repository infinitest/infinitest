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
package org.infinitest;

import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenAggregatingTestQueues {
	private List<TestQueueListener> listeners;
	private AtomicInteger finishCount;
	private List<TestQueueEvent> updateEvents;

	@BeforeEach
	void inContext() {
		ResultCollector collector = new ResultCollector();
		updateEvents = newArrayList();
		finishCount = new AtomicInteger();
		listeners = newArrayList();
		collector.addTestQueueListener(new TestQueueAdapter() {
			@Override
			public void testQueueUpdated(TestQueueEvent event) {
				updateEvents.add(event);
			}

			@Override
			public void testRunComplete() {
				finishCount.incrementAndGet();
			}
		});

		collector.attachCore(new FakeInfinitestCore() {
			@Override
			public void addTestQueueListener(TestQueueListener listener) {
				listeners.add(listener);
			}
		});

		collector.attachCore(new FakeInfinitestCore() {
			@Override
			public void addTestQueueListener(TestQueueListener listener) {
				listeners.add(listener);
			}
		});
	}

	@Test
	void shouldOnlyFireTestRunCompleteWhenAllQueuesAreEmpty() {
		listenerForCore(0).testQueueUpdated(new TestQueueEvent(asList("test1"), 1));
		listenerForCore(1).testQueueUpdated(new TestQueueEvent(asList("test3"), 1));
		listenerForCore(0).testQueueUpdated(new TestQueueEvent(Collections.<String> emptyList(), 1));
		listenerForCore(0).testRunComplete();
		assertEquals(0, finishCount.intValue());

		listenerForCore(1).testQueueUpdated(new TestQueueEvent(Collections.<String> emptyList(), 1));
		listenerForCore(1).testRunComplete();
		assertEquals(1, finishCount.intValue());
	}

	@Test
	void shouldTrackCoresUsingInternalListenerInstances() {
		assertEquals(2, listeners.size());
	}

	@Test
	void shouldCalculateTestQueueSizeBasedOnTheAggregatedQueues() {
		listenerForCore(0).testQueueUpdated(new TestQueueEvent(asList("test1", "test2"), 2));
		assertEquals(2, lastEvent().getInitialSize());
		assertEquals(2, lastEvent().getTestQueue().size());

		listenerForCore(1).testQueueUpdated(new TestQueueEvent(asList("test2", "test3"), 2));
		assertEquals(4, lastEvent().getInitialSize());
		assertEquals(4, lastEvent().getTestQueue().size());
		assertEquals(2, updateEvents.size());

		List<String> emptyQueue = emptyList();
		listenerForCore(0).testQueueUpdated(new TestQueueEvent(emptyQueue, 2));
		listenerForCore(1).testQueueUpdated(new TestQueueEvent(emptyQueue, 2));
		assertEquals(0, lastEvent().getTestQueue().size());
		assertEquals(4, lastEvent().getInitialSize());

		listenerForCore(0).testQueueUpdated(new TestQueueEvent(asList("test1", "test2"), 2));
		assertEquals(2, lastEvent().getTestQueue().size());
		assertEquals(2, lastEvent().getInitialSize());
	}

	private TestQueueEvent lastEvent() {
		return getLast(updateEvents);
	}

	private TestQueueListener listenerForCore(int coreNum) {
		return listeners.get(coreNum);
	}
}
