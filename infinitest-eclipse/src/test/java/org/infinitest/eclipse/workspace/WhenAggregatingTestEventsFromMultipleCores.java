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
package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.util.*;

import org.infinitest.*;
import org.infinitest.eclipse.*;
import org.infinitest.testrunner.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenAggregatingTestEventsFromMultipleCores {
	private TestResultAggregator aggregator;
	private InfinitestCore core;
	private List<Object> events;

	@BeforeEach
	void inContext() {
		events = new ArrayList<>();
		aggregator = new TestResultAggregator();
		core = mock(InfinitestCore.class);
	}

	@Test
	void shouldAttachToNewCores() {
		aggregator.coreCreated(core);

		verify(core).addTestResultsListener(aggregator);
	}

	@Test
	void shouldDetachFromRemovedCores() {
		aggregator.coreRemoved(core);

		verify(core).removeTestResultsListener(aggregator);
	}

	@Test
	void shouldNotifyListenersOfAggregatedEvents() {
		aggregator.addListeners(new AggregateResultsListener() {
			@Override
			public void testCaseStarting(TestEvent event) {
				events.add(event);
			}

			@Override
			public void testCaseComplete(TestCaseEvent event) {
				events.add(event);
			}
		});

		TestEvent startingEvent = testCaseStarting("Test1");
		aggregator.testCaseStarting(startingEvent);
		assertSame(startingEvent, getOnlyElement(events));

		TestCaseEvent completeEvent = new TestCaseEvent("Test1", this, new TestResults());
		aggregator.testCaseComplete(completeEvent);
		assertSame(completeEvent, get(events, 1));
	}
}
