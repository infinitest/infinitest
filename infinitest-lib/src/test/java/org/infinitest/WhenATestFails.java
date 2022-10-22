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

import static org.infinitest.CoreDependencySupport.FAILING_TEST;
import static org.infinitest.CoreDependencySupport.createCore;
import static org.infinitest.CoreDependencySupport.withChangedFiles;
import static org.infinitest.CoreDependencySupport.withTests;
import static org.infinitest.CoreStatus.FAILING;
import static org.infinitest.CoreStatus.SCANNING;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.util.InfinitestTestUtils.emptyStringList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.infinitest.testrunner.TestResultsListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenATestFails extends ResultCollectorTestSupport {
	private DefaultInfinitestCore core;
	private ControlledEventQueue eventQueue;
	private TestCaseEvent testCaseEvent;

	@BeforeEach
	void inContext() {
		eventQueue = new ControlledEventQueue();
		core = createCore(withChangedFiles(), withTests(FAILING_TEST), eventQueue);
		collector = new ResultCollector(core);
	}

	@Test
	void shouldChangeStatusToFailing() {
		testRun(methodFailed("", "", "", null));
		collector.testQueueUpdated(new TestQueueEvent(emptyStringList(), 1));
		collector.testRunComplete();
		assertEquals(FAILING, collector.getStatus());
	}

	@Test
	void shouldBeAbleToLocateTheSourceOfAnEvent() {
		core.addTestResultsListener(new TestResultsListener() {
			@Override
			public void testCaseStarting(TestEvent event) {
			}

			@Override
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
	void shouldFireFailureEvents() {
		EventSupport eventSupport = new EventSupport();
		core.addTestResultsListener(eventSupport);
		core.update();
		eventQueue.flush();
		eventSupport.assertTestsStarted(FAILING_TEST);
		eventSupport.assertTestFailed(FAILING_TEST);
		assertTrue(collector.hasFailures());
	}

	@Test
	void shouldRemoveAllFailuresOnReload() {
		shouldChangeStatusToFailing();
		collector.reloading();
		assertEquals(SCANNING, collector.getStatus());
		assertTrue(collector.getFailures().isEmpty());
		assertEquals(0, collector.getPointOfFailureCount());
		assertTrue(collector.getPointsOfFailure().isEmpty());
	}
}
