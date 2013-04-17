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
