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

import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.infinitest.testrunner.*;
import org.junit.*;
import org.junit.Test;

public class WhenWatchingMultipleCores {
	private static final String TEST_NAME = "com.fakeco.TestFoo";

	private ResultCollector collector;
	private InfinitestCore core;

	@Before
	public void inContext() {
		collector = new ResultCollector();
		core = mock(InfinitestCore.class);
	}

	@Test
	public void canAttachAndDetachFromCores() {
		collector.attachCore(core);
		collector.detachCore(core);

		verify(core).addTestQueueListener(any(TestQueueListener.class));
		verify(core).addTestResultsListener(collector);
		verify(core).addDisabledTestListener(collector);
		verify(core).removeTestQueueListener(any(TestQueueListener.class));
		verify(core).removeTestResultsListener(collector);
		verify(core).removeDisabledTestListener(collector);
	}

	@Test
	public void shouldRemoveFailuresForACoreWhenItIsDetached() {
		TestEvent event = withFailingMethod("method1");
		TestCaseEvent caseEvent = new TestCaseEvent(TEST_NAME, this, new TestResults(event));
		when(core.isEventSourceFor(caseEvent)).thenReturn(true);

		collector.testCaseComplete(caseEvent);
		assertTrue(collector.hasFailures());

		collector.detachCore(core);
		assertFalse(collector.hasFailures());

		verify(core).removeTestQueueListener(null);
		verify(core).removeTestResultsListener(collector);
		verify(core).removeDisabledTestListener(collector);
	}

	private static TestEvent withFailingMethod(String methodName) {
		return new TestEvent(METHOD_FAILURE, "", "", "", new AssertionError());
	}
}
