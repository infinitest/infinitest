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
package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.testrunner.TestResultTestUtils.assertEventsEquals;
import static org.infinitest.testrunner.TestResultTestUtils.failedMethodNames;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.infinitest.config.InfinitestConfiguration;
import org.infinitest.config.MemoryInfinitestConfigurationSource;
import org.infinitest.testrunner.exampletests.junit4.Junit4PassingTestCase;
import org.infinitest.testrunner.exampletests.junit5.JUnit5DisabledTest;
import org.infinitest.testrunner.exampletests.junit5.JUnit5Test;
import org.infinitest.testrunner.exampletests.junit5.JUnit5TestUsingComposedAnnotation;
import org.infinitest.testrunner.exampletests.junit5.JUnit5TestUsingParameterizedTest;
import org.infinitest.testrunner.exampletests.junit5.JUnit5TestUsingTag;
import org.infinitest.testrunner.junit5.Junit5Runner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import junit.framework.AssertionFailedError;

class JUnit5RunnerTest {
	private DefaultRunner runner;

	@BeforeEach
	void inContext() {
		runner = new DefaultRunner();
	}

	@Test
	void shouldDetectJUnit5Tests() {
		assertTrue(Junit5Runner.isJUnit5Test(JUnit5Test.class));
		assertFalse(Junit5Runner.isJUnit5Test(Junit4PassingTestCase.class));
	}

	@Test
	void shouldSupportJUnit5() {
		Iterable<TestEvent> events = runner.runTest(JUnit5Test.class.getName());
		TestEvent expectedEvent = methodFailed(JUnit5Test.class.getName(), "shouldFail",
				new AssertionFailedError("expected: <true> but was: <false>"));
		assertEventsEquals(expectedEvent, getOnlyElement(events));
	}

	@Test
	void shouldIgnoreDisabledTest() {
		Iterable<TestEvent> events = runner.runTest(JUnit5DisabledTest.class.getName());
		assertThat(events).isEmpty();
	}

	@Test
	void shouldSupportExcludedTags() {
		runner.setTestConfigurationSource(new MemoryInfinitestConfigurationSource(
				InfinitestConfiguration.builder().excludedGroups("Tag1").build()));

		Iterable<TestEvent> events = runner.runTest(JUnit5TestUsingTag.class.getName());

		Set<String> failedMethodNames = failedMethodNames(events);
		assertThat(failedMethodNames)
		.contains("tag2", "noTag")
		.doesNotContain("tag1", "tag1And2");
	}

	@Test
	void shouldSupportIncludeTags() {
		runner.setTestConfigurationSource(new MemoryInfinitestConfigurationSource(
				InfinitestConfiguration.builder().includedGroups("Tag2").build()));

		Iterable<TestEvent> events = runner.runTest(JUnit5TestUsingTag.class.getName());
		Set<String> failedMethodNames = failedMethodNames(events);

		assertThat(failedMethodNames)
		.contains("tag2", "tag1And2")
		.doesNotContain("tag1", "noTag");

	}

	@Test
	void shouldSupportIncludeCombinedwithExcludeTags() {
		runner.setTestConfigurationSource(new MemoryInfinitestConfigurationSource(
				InfinitestConfiguration.builder().includedGroups("Tag2").excludedGroups("Tag1").build()));

		Iterable<TestEvent> events = runner.runTest(JUnit5TestUsingTag.class.getName());
		Set<String> failedMethodNames = failedMethodNames(events);

		assertThat(failedMethodNames)
		.contains("tag2")
		.doesNotContain("tag1", "noTag", "tag1And2");

	}
	
	@Test
	void shouldSupportCombinedAnnotation() {
		assertTrue(Junit5Runner.isJUnit5Test(JUnit5TestUsingComposedAnnotation.class));
		
		Iterable<TestEvent> events = runner.runTest(JUnit5TestUsingComposedAnnotation.class.getName());
		Set<String> failedMethodNames = failedMethodNames(events);

		assertThat(failedMethodNames).contains("fastTest");
	}
	
	@Test
	void shouldSupportParameterizedTest() {
		assertTrue(Junit5Runner.isJUnit5Test(JUnit5TestUsingComposedAnnotation.class));
		
		Iterable<TestEvent> events = runner.runTest(JUnit5TestUsingParameterizedTest.class.getName());
		Set<String> failedMethodNames = failedMethodNames(events);

		assertThat(failedMethodNames).isEmpty();
	}
}
