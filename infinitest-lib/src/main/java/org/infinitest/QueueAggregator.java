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
