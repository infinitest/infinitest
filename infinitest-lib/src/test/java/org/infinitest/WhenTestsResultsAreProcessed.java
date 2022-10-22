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

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.CoreStatus.PASSING;
import static org.infinitest.CoreStatus.RUNNING;
import static org.infinitest.CoreStatus.SCANNING;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.util.InfinitestTestUtils.emptyStringList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

class WhenTestsResultsAreProcessed extends ResultCollectorTestSupport {
	private EventSupport statusListener;

	@BeforeEach
	void inContext() {
		statusListener = new EventSupport();
	}

	@Test
	void canAttachToCore() {
		InfinitestCore core = mock(InfinitestCore.class);

		new ResultCollector(core);

		verify(core).addTestQueueListener(any(TestQueueListener.class));
		verify(core).addTestResultsListener(any(ResultCollector.class));
		verify(core).addDisabledTestListener(any(DisabledTestListener.class));
	}

	@Test
	void shouldListenToCoreForResults() {
		assertEquals(SCANNING, collector.getStatus());
	}

	@Test
	void shouldChangeStatusToRunning() {
		collector.addStatusChangeListener(statusListener);
		collector.testQueueUpdated(new TestQueueEvent(asList("aTest"), 1));
		collector.testQueueUpdated(new TestQueueEvent(emptyStringList(), 1));
		statusListener.assertStatesChanged(RUNNING);

		collector.testRunComplete();
		statusListener.assertStatesChanged(RUNNING, PASSING);
		assertEquals(PASSING, collector.getStatus());
	}

	@Test
	void shouldNotifyTestCollectorOfTestEvents() {
		FailureListenerSupport listener = new FailureListenerSupport();
		collector.addChangeListener(listener);
		TestEvent failureEvent = methodFailed("message", DEFAULT_TEST_NAME, "methodName", new AssertionError());

		testRun(failureEvent);
		assertEquals(failureEvent, getOnlyElement(collector.getFailures()));
		assertEquals(failureEvent, getOnlyElement(listener.added));
		assertThat(listener.removed).isEmpty();

		testRunWith("testName2");
		assertThat(listener.removed).isEmpty();

		listener.added.clear();
		testRun();
		// collector.testCaseFinished(testCaseFinished("testName"));
		assertEquals(failureEvent, getOnlyElement(listener.removed));
		assertThat(listener.added).isEmpty();
	}

	@Test
	void shouldIgnoreDisabledTestsThatArentFailing() {
		collector.testsDisabled(asList("NotAFailingTest"));
	}

	@Test
	void shouldClearFailuresForDisabledTests() {
		String testName = "MyClass";
		testRunWith(testName, methodFailed(testName, "someMethod", new NullPointerException()));
		assertEquals(1, collector.getFailures().size());

		final Collection<TestEvent> removed = Lists.newArrayList();
		collector.addChangeListener(new FailureListListener() {
			@Override
			public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
				removed.addAll(failuresRemoved);
			}

			@Override
			public void failuresUpdated(Collection<TestEvent> updatedFailures) {
				throw new UnsupportedOperationException();
			}
		});
		collector.testsDisabled(asList(testName));
		assertEquals(0, collector.getFailures().size());
		assertEquals("MyClass", getOnlyElement(removed).getTestName());
	}
}
