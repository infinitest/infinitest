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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Comparator;

import org.infinitest.testrunner.TestResultsListener;
import org.infinitest.testrunner.TestRunner;
import org.junit.jupiter.api.Test;

class WhenATestIsRun {
	@Test
	void shouldEvent() {
		EventNormalizer normalizer = new EventNormalizer(new ControlledEventQueue());
		assertNotNull(normalizer.consoleEventNormalizer(new ConsoleListenerAdapter()));
	}

	@Test
	void shouldFireEventsForConsoleUpdates() {
		TestRunner runner = mock(TestRunner.class);

		DefaultInfinitestCore core = new DefaultInfinitestCore(runner, new ControlledEventQueue());
		ConsoleListenerAdapter listener = new ConsoleListenerAdapter();
		core.addConsoleOutputListener(listener);
		core.removeConsoleOutputListener(listener);

		verify(runner).addTestResultsListener(any(TestResultsListener.class));
		verify(runner).setTestPriority(any(Comparator.class));
		verify(runner).addConsoleOutputListener(any(ConsoleOutputListener.class));
		verify(runner).removeConsoleOutputListener(any(ConsoleOutputListener.class));
	}
}
