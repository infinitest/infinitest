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

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runner.notification.*;

/**
 * Exploratory test for JunitCore
 * 
 * @author bjrady
 * 
 */
public class TestJunitCore {
	private List<Description> finishedList;
	private JUnitCore core;
	private ArrayList<Failure> failingList;

	@Before
	public void whenCoreHasListeners() {
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
		core.run(StubTest.class);
	}

	@After
	public void cleanup() {
		core = null;
		finishedList = null;
		StubTest.disable();
	}

	@Test
	public void shouldRunTestsAndReportResults() {
		Description desc = findDescription("shouldBeSucessful", finishedList);
		assertEquals("Display name", "shouldBeSucessful(org.infinitest.TestJunitCore$StubTest)", desc.getDisplayName());
		assertTrue("Result should have no children", desc.getChildren().isEmpty());
		assertTrue("Test result, not suite result", desc.isTest());
		assertEquals("Test Count", 1, desc.testCount());

		desc = findDescription("shouldFailIfPropertyIsSet", finishedList);
		assertEquals("Display name", "shouldFailIfPropertyIsSet(org.infinitest.TestJunitCore$StubTest)", desc.getDisplayName());
		assertTrue("Result should have no children", desc.getChildren().isEmpty());
		assertTrue("Test result, not suite result", desc.isTest());
		assertEquals("Test Count", 1, desc.testCount());

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
	public void shouldNotifyListenersAboutFailingTests() {
		assertEquals(1, failingList.size());
		Failure failure = failingList.get(0);
		assertEquals("shouldFailIfPropertyIsSet(org.infinitest.TestJunitCore$StubTest)", failure.getTestHeader());
		assertEquals("This test should fail", failure.getMessage());
	}

	public static class StubTest {
		@Test
		@SuppressWarnings("all")
		public void shouldBeSucessful() {
		}

		@Test
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
