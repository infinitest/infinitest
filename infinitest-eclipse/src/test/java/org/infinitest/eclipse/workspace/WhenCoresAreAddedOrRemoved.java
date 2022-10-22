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
package org.infinitest.eclipse.workspace;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.TestQueueListener;
import org.infinitest.eclipse.CoreLifecycleObserver;
import org.infinitest.eclipse.SlowTestObserver;
import org.infinitest.eclipse.console.ConsolePopulatingListener;
import org.junit.jupiter.api.Test;

class WhenCoresAreAddedOrRemoved {
	private InfinitestCore core;
	private CoreLifecycleObserver observer;

	@Test
	void shouldAttachAndDetachListenersFromCore() {
		ResultCollector resultCollector = new ResultCollector();
		SlowTestObserver slowTestObserver = mock(SlowTestObserver.class);
		core = mock(InfinitestCore.class);

		observer = new CoreLifecycleObserver(resultCollector, slowTestObserver);
		observer.coreCreated(core);
		observer.coreRemoved(core);

		verify(core).addTestResultsListener(slowTestObserver);
		verify(core).addTestResultsListener(resultCollector);
		verify(core).addDisabledTestListener(slowTestObserver);
		verify(core).addDisabledTestListener(resultCollector);
		verify(core, times(2)).addTestQueueListener(any(TestQueueListener.class));
		verify(core).addConsoleOutputListener(any(ConsolePopulatingListener.class));

		verify(core).removeTestResultsListener(slowTestObserver);
		verify(core).removeTestResultsListener(resultCollector);
		verify(core).removeDisabledTestListener(slowTestObserver);
		verify(core).removeDisabledTestListener(resultCollector);
		verify(core, times(2)).removeTestQueueListener(any(TestQueueListener.class));
		verify(core).removeConsoleOutputListener(any(ConsolePopulatingListener.class));
	}
}
