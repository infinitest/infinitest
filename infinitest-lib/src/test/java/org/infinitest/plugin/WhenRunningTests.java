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
package org.infinitest.plugin;

import static org.infinitest.CoreStatus.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.infinitest.*;
import org.junit.*;

import com.fakeco.fakeproduct.simple.*;

public class WhenRunningTests {
	private static EventSupport eventHistory;
	private static ResultCollector collector;

	@BeforeClass
	public static void inContext() throws InterruptedException {
		if (eventHistory == null) {
			InfinitestCoreBuilder builder = new InfinitestCoreBuilder(fakeEnvironment(), new FakeEventQueue());
			builder.setUpdateSemaphore(mock(ConcurrencyController.class));
			builder.setFilter(new InfinitestTestFilter());
			InfinitestCore core = builder.createCore();
			collector = new ResultCollector(core);

			eventHistory = new EventSupport();
			core.addTestResultsListener(eventHistory);
			core.addTestQueueListener(eventHistory);

			core.update();
			eventHistory.assertRunComplete();
		}
	}

	@Test
	public void canListenForResultEvents() {
		eventHistory.assertTestFailed(FailingTest.class);
		eventHistory.assertTestPassed(PassingTest.class);
	}

	// This will frequently hang when something goes horribly wrong in the test
	// runner
	@Test(timeout = 5000)
	public void canListenForChangesToTheTestQueue() throws Exception {
		eventHistory.assertQueueChanges(3);
	}

	@Test
	public void canCollectStateUsingAResultCollector() {
		assertEquals(1, collector.getFailures().size());
		assertEquals(FAILING, collector.getStatus());
	}
}