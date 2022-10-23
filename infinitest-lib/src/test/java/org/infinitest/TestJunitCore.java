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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Exploratory test for JunitCore
 * 
 * @author bjrady
 * 
 */
class TestJunitCore {
	private List<Description> finishedList;
	private JUnitCore core;
	private ArrayList<Failure> failingList;

	@BeforeEach
	void whenCoreHasListeners() {
		core = new JUnitCore();
		finishedList = new ArrayList<Description>();
		failingList = new ArrayList<Failure>();
		core.addListener(new RunListener() {
			@Override
			public void testFinished(Description description) {
				finishedList.add(description);
			}

			@Override
			public void testFailure(Failure failure) {
				failingList.add(failure);
			}
		});
		StubTest.enable();
		core.run(new Class[] { StubTest.class });
	}

	@AfterEach
	void cleanup() {
		core = null;
		finishedList = null;
		StubTest.disable();
	}

	@Test
	void shouldRunTestsAndReportResults() {
		Description desc = findDescription("shouldBeSucessful", finishedList);
		assertEquals("shouldBeSucessful(org.infinitest.TestJunitCore$StubTest)", desc.getDisplayName(), "Display name");
		assertThat(desc.getChildren()).as("Result should have no children").isEmpty();
		assertThat(desc.isTest()).as("Test result, not suite result").isTrue();
		assertEquals(1, desc.testCount(), "Test Count");

		desc = findDescription("shouldFailIfPropertyIsSet", finishedList);
		assertEquals("shouldFailIfPropertyIsSet(org.infinitest.TestJunitCore$StubTest)", desc.getDisplayName(), "Display name");
		assertThat(desc.getChildren()).as("Result should have no children").isEmpty();
		assertThat(desc.isTest()).as("Test result, not suite result").isTrue();
		assertEquals(1, desc.testCount(), "Test Count");

		assertEquals(2, finishedList.size());
	}

	private static Description findDescription(String methodName, List<Description> testList) {
		for (Description d : testList) {
			if (d.getDisplayName().startsWith(methodName + "(")) {
				return d;
			}
		}
		return null;
	}

	@Test
	void shouldNotifyListenersAboutFailingTests() {
		assertEquals(1, failingList.size());
		Failure failure = failingList.get(0);
		assertEquals("shouldFailIfPropertyIsSet(org.infinitest.TestJunitCore$StubTest)", failure.getTestHeader());
		assertEquals("This test should fail", failure.getMessage());
	}

	public static class StubTest {
		@org.junit.Test
		@SuppressWarnings("all")
		public void shouldBeSucessful() {
		}

		@org.junit.Test
		public void shouldFailIfPropertyIsSet() {
			// This is done so Eclipse doesn't get confused and fail because of
			// this test.
			if (System.getProperty(StubTest.class.getName()) != null) {
				fail("This test should fail");
			}
		}

		public static void enable() {
			System.setProperty(StubTest.class.getName(), "");
		}

		public static void disable() {
			System.clearProperty(StubTest.class.getName());
		}
	}
}
