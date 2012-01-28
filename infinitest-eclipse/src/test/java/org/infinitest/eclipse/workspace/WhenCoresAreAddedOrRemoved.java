/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.eclipse.workspace;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.infinitest.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.console.*;
import org.junit.*;

public class WhenCoresAreAddedOrRemoved {
	private InfinitestCore core;
	private CoreLifecycleObserver observer;

	@Test
	public void shouldAttachAndDetachListenersFromCore() {
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
