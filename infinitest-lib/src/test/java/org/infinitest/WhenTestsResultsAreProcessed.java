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
import static org.infinitest.CoreStatus.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.infinitest.testrunner.*;
import org.junit.*;

import com.google.common.collect.*;

public class WhenTestsResultsAreProcessed extends ResultCollectorTestSupport {
	private EventSupport statusListener;

	@Before
	public void inContext() {
		statusListener = new EventSupport();
	}

	@Test
	public void canAttachToCore() {
		InfinitestCore core = mock(InfinitestCore.class);

		new ResultCollector(core);

		verify(core).addTestQueueListener(any(TestQueueListener.class));
		verify(core).addTestResultsListener(any(ResultCollector.class));
		verify(core).addDisabledTestListener(any(DisabledTestListener.class));
	}

	@Test
	public void shouldListenToCoreForResults() {
		assertEquals(SCANNING, collector.getStatus());
	}

	@Test
	public void shouldChangeStatusToRunning() {
		collector.addStatusChangeListener(statusListener);
		collector.testQueueUpdated(new TestQueueEvent(asList("aTest"), 1));
		collector.testQueueUpdated(new TestQueueEvent(Collections.<String>emptyList(), 1));
		statusListener.assertStatesChanged(RUNNING);

		collector.testRunComplete();
		statusListener.assertStatesChanged(RUNNING, PASSING);
		assertEquals(PASSING, collector.getStatus());
	}

	@Test
	public void shouldNotifyTestCollectorOfTestEvents() {
		FailureListenerSupport listener = new FailureListenerSupport();
		collector.addChangeListener(listener);
		TestEvent failureEvent = methodFailed("message", DEFAULT_TEST_NAME, "methodName", new AssertionError());

		testRun(failureEvent);
		assertEquals(failureEvent, getOnlyElement(collector.getFailures()));
		assertEquals(failureEvent, getOnlyElement(listener.added));
		assertTrue(listener.removed.isEmpty());

		testRunWith("testName2");
		assertTrue(listener.removed.isEmpty());

		listener.added.clear();
		testRun();
		// collector.testCaseFinished(testCaseFinished("testName"));
		assertEquals(failureEvent, getOnlyElement(listener.removed));
		assertTrue(listener.added.isEmpty());
	}

	@Test
	public void shouldIgnoreDisabledTestsThatArentFailing() {
		collector.testsDisabled(asList("NotAFailingTest"));
	}

	@Test
	public void shouldClearFailuresForDisabledTests() {
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
