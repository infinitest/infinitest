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
package org.infinitest;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.junit.*;

public class WhenAggregatingTestQueues {
	private List<TestQueueListener> listeners;
	private AtomicInteger finishCount;
	private List<TestQueueEvent> updateEvents;

	@Before
	public void inContext() {
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
	public void shouldOnlyFireTestRunCompleteWhenAllQueuesAreEmpty() {
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
	public void shouldTrackCoresUsingInternalListenerInstances() {
		assertEquals(2, listeners.size());
	}

	@Test
	public void shouldCalculateTestQueueSizeBasedOnTheAggregatedQueues() {
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
