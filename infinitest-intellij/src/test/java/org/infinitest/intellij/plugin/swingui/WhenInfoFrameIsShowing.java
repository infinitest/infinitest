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
package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.intellij.plugin.swingui.EventInfoFrame.stackTraceToString;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.testrunner.TestEvent.TestState.TEST_CASE_STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.Test;

class WhenInfoFrameIsShowing {
	@Test
	void shouldCloseWithEscapeKey() {
		EventInfoFrame frame = new EventInfoFrame(withATest());
		assertEquals("ESCAPE", frame.getRootPane().getActionMap().keys()[0]);
	}

	@Test
	void shouldReturnEmptyStringForNullStackTrace() {
		assertEquals("", stackTraceToString(null));
	}

	@Test
	void shouldLimitStackTraceStringsTo50Lines() {
		List<StackTraceElement> elements = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			elements.add(new StackTraceElement("class", "method", "file", 0));
		}
		StackTraceElement[] traceElements = elements.toArray(new StackTraceElement[0]);
		String[] lines = stackTraceToString(traceElements).split("\\n");
		assertEquals(51, lines.length);
		assertEquals("50 more...", lines[50]);
	}

	static void main(String[] args) {
		AssertionError assertionError = new AssertionError("This is a very long error message. Who would type such a message? It's crazy. This is much too long. This must be stopped. It cannot be allowed to continue.");
		EventInfoFrame frame = new EventInfoFrame(methodFailed("message", "", assertionError));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static TestEvent withATest() {
		return new TestEvent(TEST_CASE_STARTING, "", "", "", null);
	}
}
