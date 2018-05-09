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
package org.infinitest.testrunner.junit5;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.infinitest.testrunner.MethodStats;
import org.infinitest.testrunner.StubClock;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestEvent.TestState;
import org.infinitest.testrunner.TestResults;
import org.infinitest.testrunner.exampletests.junit5.JUnit5Test;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.TestIdentifier;
import org.opentest4j.AssertionFailedError;

public class JUnit5EventTranslatorTest {

	private StubClock stubClock;
	private JUnit5EventTranslator eventTranslator;
	private TestMethodTestDescriptor shouldFailMethodTestDescriptor;

	@Before
	public void before() throws NoSuchMethodException, SecurityException {
		stubClock = new StubClock();
		eventTranslator = new JUnit5EventTranslator(stubClock);

		UniqueId id = UniqueId.parse(
				"[engine:junit-jupiter]/[class:org.infinitest.testrunner.exampletests.junit5.JUnit5Test]/[method:shouldFail()]");
		shouldFailMethodTestDescriptor = new TestMethodTestDescriptor(id, JUnit5Test.class,
				JUnit5Test.class.getMethod("shouldFail"));
	}

	@Test
	public void shouldReturnEmptyTestResultsWhenNoExecutedTests() {
		TestResults results = eventTranslator.getTestResults();
		assertTrue(isEmpty(results));
	}

	@Test
	public void shouldCollectMethodStatisticsForFailedTests() {
		stubClock.time = 10;
		eventTranslator.executionStarted(TestIdentifier.from(shouldFailMethodTestDescriptor));
		stubClock.time = 20;
		Throwable assertionError = new AssertionFailedError("failure");
		eventTranslator.executionFinished(TestIdentifier.from(shouldFailMethodTestDescriptor),
				TestExecutionResult.failed(assertionError));
		TestResults results = eventTranslator.getTestResults();
		MethodStats methodStats = getOnlyElement(results.getMethodStats());
		assertEquals(10, methodStats.startTime);
		assertEquals(20, methodStats.stopTime);
	}

	@Test
	public void shouldCollectEventsForFailedTests() {
		eventTranslator.executionStarted(TestIdentifier.from(shouldFailMethodTestDescriptor));

		Throwable assertionError = new AssertionFailedError("failure");
		eventTranslator.executionFinished(TestIdentifier.from(shouldFailMethodTestDescriptor),
				TestExecutionResult.failed(assertionError));

		assertThat(eventTranslator.getTestResults()).hasSize(1);
		TestEvent event = eventTranslator.getTestResults().iterator().next();
		assertThat(event.getTestMethod()).isEqualTo("shouldFail");
		assertThat(event.getType()).isEqualTo(TestState.METHOD_FAILURE);
		assertThat(event.getErrorClassName()).isEqualTo(assertionError.getClass().getSimpleName());
		assertThat(event.getMessage()).isEqualTo(assertionError.getMessage());
		assertThat(event.getStackTrace()).isEqualTo(assertionError.getStackTrace());
	}

	@Test
	public void shouldCollectMethodStatisticsForSuccessfulTests() {
		stubClock.time = 10;
		eventTranslator.executionStarted(TestIdentifier.from(shouldFailMethodTestDescriptor));
		stubClock.time = 20;
		eventTranslator.executionFinished(TestIdentifier.from(shouldFailMethodTestDescriptor),
				TestExecutionResult.successful());
		TestResults results = eventTranslator.getTestResults();
		MethodStats methodStats = getOnlyElement(results.getMethodStats());
		assertEquals(10, methodStats.startTime);
		assertEquals(20, methodStats.stopTime);
	}

	@Test
	public void shouldNotCollectEventForSucessfulTests() {
		eventTranslator.executionStarted(TestIdentifier.from(shouldFailMethodTestDescriptor));

		eventTranslator.executionFinished(TestIdentifier.from(shouldFailMethodTestDescriptor),
				TestExecutionResult.successful());

		assertThat(eventTranslator.getTestResults()).isEmpty();
	}
}
