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

import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenATestFails extends ResultCollectorTestSupport {
	private DefaultInfinitestCore core;
	private ControlledEventQueue eventQueue;
	private TestCaseEvent testCaseEvent;

	@Before
	public void inContext() {
		eventQueue = new ControlledEventQueue();
		core = createCore(withChangedFiles(), withTests(FAILING_TEST), eventQueue);
		collector = new ResultCollector(core);
	}

	@Test
	public void shouldChangeStatusToFailing() {
		testRun(methodFailed("", "", "", null));
		collector.testQueueUpdated(new TestQueueEvent(emptyStringList(), 1));
		collector.testRunComplete();
		assertEquals(FAILING, collector.getStatus());
	}

	@Test
	public void shouldBeAbleToLocateTheSourceOfAnEvent() {
		core.addTestResultsListener(new TestResultsListener() {
			public void testCaseStarting(TestEvent event) {
			}

			public void testCaseComplete(TestCaseEvent event) {
				testCaseEvent = event;
			}
		});
		core.update();
		eventQueue.flush();

		assertTrue(core.isEventSourceFor(testCaseEvent));
		TestResults resultCopy = new TestResults(testCaseEvent.getFailureEvents());
		TestCaseEvent altEvent = new TestCaseEvent(testCaseEvent.getTestName(), this, resultCopy);
		assertFalse(core.isEventSourceFor(altEvent));
	}

	@Test
	public void shouldFireFailureEvents() {
		EventSupport eventSupport = new EventSupport();
		core.addTestResultsListener(eventSupport);
		core.update();
		eventQueue.flush();
		eventSupport.assertTestsStarted(FAILING_TEST);
		eventSupport.assertTestFailed(FAILING_TEST);
		assertTrue(collector.hasFailures());
	}

	@Test
	public void shouldRemoveAllFailuresOnReload() {
		shouldChangeStatusToFailing();
		collector.reloading();
		assertEquals(SCANNING, collector.getStatus());
		assertTrue(collector.getFailures().isEmpty());
		assertEquals(0, collector.getPointOfFailureCount());
		assertTrue(collector.getPointsOfFailure().isEmpty());
	}
}
