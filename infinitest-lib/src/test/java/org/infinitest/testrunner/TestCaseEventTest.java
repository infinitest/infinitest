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

import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.infinitest.util.EqualityTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestCaseEventTest extends EqualityTestSupport {
	private TestCaseEvent event;
	private List<TestEvent> methodEvents;
	private Object source;

	@BeforeEach
	void inContext() {
		methodEvents = new ArrayList<>();
		source = "";
		event = new TestCaseEvent("testName", source, new TestResults(methodEvents));
	}

	@Test
	void shouldIgnoreCompilerErrors() {
		methodEvents.add(methodFailed("TestClass", "", new VerifyError()));
		methodEvents.add(methodFailed("TestClass", "", new Error("Unresolved compilation problems:")));
		event = new TestCaseEvent("testName", source, new TestResults(methodEvents));
		assertFalse(event.failed());
	}

	@Test
	void shouldContainMethodEvents() {
		assertFalse(event.failed());
	}

	@Test
	void shouldIndicateSourceOfEvent() {
		assertEquals(source, event.getSource());
	}

	@Override
	protected TestCaseEvent createEqualInstance() {
		return new TestCaseEvent("testName", source, new TestResults());
	}

	@Override
	protected Object createUnequalInstance() {
		return new TestCaseEvent("testName2", source, new TestResults());
	}
}
