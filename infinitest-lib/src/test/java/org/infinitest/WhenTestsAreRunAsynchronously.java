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
package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;

import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenTestsAreRunAsynchronously {
	private DefaultInfinitestCore core;
	private EventSupport eventSupport;

	@Before
	public void inContext() {
		core = createAsyncCore(withChangedFiles(), withTests(FAILING_TEST, PASSING_TEST, TestJUnit4TestCase.class));
		eventSupport = new EventSupport(5000);
		core.addTestQueueListener(eventSupport);
		core.addTestResultsListener(eventSupport);
		// Not sure why, but maven fails if we don't reset the interrupted state
		// here!
		Thread.interrupted();
	}

	@Test
	public void canUpdateWhileTestsAreRunning() throws Exception {
		core.update();
		eventSupport.assertQueueChanges(1);
		// There are three tests in the queue, we're updating before they're all
		// finished
		core.update();

		eventSupport.assertQueueChanges(3);
		eventSupport.assertRunComplete();
	}
}
