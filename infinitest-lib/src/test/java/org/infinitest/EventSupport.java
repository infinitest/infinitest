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

import static com.google.common.collect.Iterables.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.infinitest.testrunner.*;
import org.infinitest.testrunner.TestEvent.TestState;
import org.infinitest.util.*;

public class EventSupport implements StatusChangeListener, TestQueueListener, TestResultsListener {
	private final List<TestEvent> testEvents;
	private final Map<String, TestCaseEvent> testCaseEvents;
	private final List<PropertyChangeEvent> propertyEvents;
	private ThreadSafeFlag runComplete;
	private final List<TestQueueEvent> queueEvents;
	private final long timeout;
	private final ThreadSafeFlag reload;
	private int reloadCount;

	public EventSupport(long timeout) {
		testCaseEvents = new HashMap<>();
		testEvents = new ArrayList<>();
		propertyEvents = new ArrayList<>();
		queueEvents = new ArrayList<>();
		runComplete = new ThreadSafeFlag(timeout);
		reload = new ThreadSafeFlag(timeout);
		this.timeout = timeout;
	}

	public EventSupport() {
		this(5000);
	}

	@Override
	public void testCaseStarting(TestEvent event) {
		testEvents.add(event);
	}

	@Override
	public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus) {
		propertyEvents.add(new PropertyChangeEvent(this, "doesn't matter", oldStatus, newStatus));
	}

	@Override
	public void testRunComplete() {
		runComplete.trip();
	}

	@Override
	public void testQueueUpdated(TestQueueEvent event) {
		queueEvents.add(event);
	}

	@Override
	public void reloading() {
		reload.trip();
		reloadCount++;
	}

	public void assertEventsReceived(TestState... expectedTypes) {
		ArrayList<TestState> actualTypes = new ArrayList<TestState>();
		for (TestEvent event : testEvents) {
			actualTypes.add(event.getType());
		}
		assertThat(actualTypes).isEqualTo(asList(expectedTypes));
	}

	public void reset() {
		resetEvents();
	}

	public void resetEvents() {
		testEvents.clear();
		testCaseEvents.clear();
	}

	public void assertStatesChanged(CoreStatus... expectedStates) {
		ArrayList<CoreStatus> actualStates = new ArrayList<CoreStatus>();
		for (PropertyChangeEvent event : propertyEvents) {
			actualStates.add((CoreStatus) event.getNewValue());
		}
		assertThat(actualStates).isEqualTo(asList(expectedStates));
	}

	public void assertTestRun(Class<?> clazz) {
		assertTestRun(clazz.getName());
	}

	public void assertTestRun(String name) {
		TestCaseEvent event = testCaseEvents.get(name);
		assertThat(event).as(name + " was never run!").isNotNull();
	}

	public void assertTestPassed(String name) {
		assertTestRun(name);
		assertThat(testCaseEvents.get(name).failed()).as(name + " failed! Expected to pass").isFalse();
	}

	public void assertTestFailed(String name) {
		assertTestRun(name);
		assertThat(testCaseEvents.get(name).failed()).as(name + " failed! Expected to pass").isTrue();
	}

	public void assertRunComplete() throws InterruptedException {
		runComplete.assertTripped();
		resetRunComplete();
	}

	public void assertQueueChanges(int expectedEventCount) throws InterruptedException {
		Awaitility.await().atMost(timeout, TimeUnit.MILLISECONDS).until(() -> queueEvents.size() >= expectedEventCount);
	}

	public void assertReloadOccured() throws InterruptedException {
		reload.assertTripped();
	}

	public int getReloadCount() {
		return reloadCount;
	}

	public void assertTestPassed(Class<?> clazz) {
		assertTestPassed(clazz.getName());
	}

	public void assertTestFailed(Class<?> clazz) {
		assertTestFailed(clazz.getName());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (TestEvent each : testEvents) {
			builder.append(each.toString() + each.getErrorClassName() + each.getMessage());
		}
		return builder.toString();
	}

	public Iterable<String> getTestsRun() {
		Set<String> tests = new HashSet<>();
		for (TestEvent event : testEvents) {
			tests.add(event.getTestName());
		}
		return tests;
	}

	private void resetRunComplete() {
		runComplete = new ThreadSafeFlag(timeout);
	}

	@Override
	public void testCaseComplete(TestCaseEvent event) {
		for (TestEvent each : event.getFailureEvents()) {
			testEvents.add(each);
		}

		testCaseEvents.put(event.getTestName(), event);
	}

	public static TestCaseEvent testCaseFailing(String testName, String methodName, Throwable throwable) {
		return new TestCaseEvent(testName, new Object(), new TestResults(methodFailed(testName, methodName, throwable)));
	}

	public void assertTestsStarted(String... testNames) {
		// Also asserts the order
		List<String> startedTests = new ArrayList<String>();
		for (TestEvent event : testEvents) {
			if (event.getType().equals(TEST_CASE_STARTING)) {
				startedTests.add(event.getTestName());
			}
		}
		assertEquals(asList(testNames), startedTests);
	}

	public void assertTestsStarted(Class<?>... classes) {
		List<String> testNames = new ArrayList<>();
		for (Class<?> each : classes) {
			testNames.add(each.getName());
		}

		assertTestsStarted(toArray(testNames, String.class));
	}
}
