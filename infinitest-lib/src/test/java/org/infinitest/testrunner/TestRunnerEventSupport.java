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
package org.infinitest.testrunner;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.*;

import org.infinitest.*;
import org.junit.*;

public class TestRunnerEventSupport {
	private int events;
	private RunnerEventSupport eventSupport;

	@Before
	public void inContext() {
		eventSupport = new RunnerEventSupport(this);
	}

	@Test
	public void shouldFireEventsWhenTestRunFinishes() {
		eventSupport.addTestQueueListener(new TestQueueAdapter() {
			@Override
			public void testRunComplete() {
				events++;
			}
		});
		eventSupport.fireTestRunComplete();
		assertEquals(1, events);
	}

	@Test
	public void shouldFireTestQueueEvents() {
		final List<TestQueueEvent> queueEvents = newArrayList();
		eventSupport.addTestQueueListener(new TestQueueAdapter() {
			@Override
			public void testQueueUpdated(TestQueueEvent event) {
				queueEvents.add(event);
			}
		});
		TestQueueEvent event = new TestQueueEvent(asList("MyTest"), 1);
		eventSupport.fireQueueEvent(event);
		assertThat(queueEvents, hasItem(event));
	}
}
