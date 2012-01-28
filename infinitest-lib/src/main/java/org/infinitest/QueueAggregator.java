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

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static java.lang.Math.*;

import java.util.*;

public class QueueAggregator {
	private int initialSize = 0;
	private final Map<InfinitestCore, AggregatingQueueListener> coreQueueListeners;
	public static final String STATUS_PROPERTY = "status";
	private final List<TestQueueListener> queueListeners;

	public QueueAggregator() {
		queueListeners = newArrayList();
		coreQueueListeners = newLinkedHashMap();
	}

	public void addListener(TestQueueListener testQueueAdapter) {
		queueListeners.add(testQueueAdapter);
	}

	public void removeTestQueueListener(TestQueueListener listener) {
		queueListeners.remove(listener);
	}

	public void attach(InfinitestCore core) {
		AggregatingQueueListener listener = new AggregatingQueueListener();
		coreQueueListeners.put(core, listener);
		core.addTestQueueListener(listener);
	}

	public void detach(InfinitestCore core) {
		core.removeTestQueueListener(coreQueueListeners.remove(core));
	}

	private void fireTestQueueEvent() {
		List<String> aggregatedQueue = getAggregatedQueue();
		initialSize = max(initialSize, aggregatedQueue.size());
		for (TestQueueListener each : queueListeners) {
			each.testQueueUpdated(new TestQueueEvent(aggregatedQueue, initialSize));
		}
		if (aggregatedQueue.isEmpty()) {
			initialSize = 0;
		}
	}

	private List<String> getAggregatedQueue() {
		List<String> aggregatedQueue = newArrayList();
		for (AggregatingQueueListener each : coreQueueListeners.values()) {
			aggregatedQueue.addAll(each.currentQueue);
		}
		return aggregatedQueue;
	}

	private void fireReloadingEvent() {
		for (TestQueueListener each : queueListeners) {
			each.reloading();
		}
	}

	private void fireCompleteEvent() {
		if (getAggregatedQueue().isEmpty()) {
			for (TestQueueListener each : queueListeners) {
				each.testRunComplete();
			}
		}
	}

	Map<InfinitestCore, AggregatingQueueListener> getCoreQueueListeners() {
		return coreQueueListeners;
	}

	private class AggregatingQueueListener extends TestQueueAdapter {
		private List<String> currentQueue = newArrayList();

		@Override
		public void reloading() {
			fireReloadingEvent();
		}

		@Override
		public void testRunComplete() {
			fireCompleteEvent();
		}

		@Override
		public void testQueueUpdated(TestQueueEvent event) {
			currentQueue = event.getTestQueue();
			fireTestQueueEvent();
		}
	}

}
