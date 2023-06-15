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

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import junit.framework.AssertionFailedError;

class WhenWatchingMultipleCores {
	private static final String TEST_NAME = "com.fakeco.TestFoo";
	private static final String FAILING_TEST_NAME = "com.fakeco.FailingTestFoo";

	private ResultCollector collector;
	private InfinitestCore core;

	@BeforeEach
	void inContext() {
		collector = new ResultCollector();
		core = mock(InfinitestCore.class);
	}

	@Test
	void canAttachAndDetachFromCores() {
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
	void shouldRemoveFailuresForACoreWhenItIsDetached() {
		addTestEvent(core, TEST_NAME, withFailingMethod("method1"));
		assertTrue(collector.hasFailures());

		collector.detachCore(core);
		assertFalse(collector.hasFailures());

		verify(core).removeTestQueueListener(null);
		verify(core).removeTestResultsListener(collector);
		verify(core).removeDisabledTestListener(collector);
		
		// There was only that core, there shouldn't be any test results left so the status should be SCANNING
		assertThat(collector.getStatus()).isEqualTo(CoreStatus.SCANNING);
	}
	
	@Test
	void shouldUpdateStatusWhenRemovingOnlyCoreWithFailureAndTheresAnotherCoreWithPassingTests() {
		InfinitestCore passingCore = mock(InfinitestCore.class);
		
		addTestEvent(core, TEST_NAME, withFailingMethod("method1"));
		addTestEvent(passingCore, FAILING_TEST_NAME);
		
		collector.detachCore(core);
		
		// There should be one test passing test result so the status should be PASSING
		assertThat(collector.getStatus()).isEqualTo(CoreStatus.PASSING);
	}

	private void addTestEvent(InfinitestCore c, String testName, TestEvent ... events) {
		TestCaseEvent caseEvent = new TestCaseEvent(testName, this, new TestResults(events));
		when(c.isEventSourceFor(caseEvent)).thenReturn(true);

		collector.testCaseComplete(caseEvent);
	}

	private static TestEvent withFailingMethod(String methodName) {
		return new TestEvent(METHOD_FAILURE, "", "", methodName, new AssertionFailedError());
	}
}
