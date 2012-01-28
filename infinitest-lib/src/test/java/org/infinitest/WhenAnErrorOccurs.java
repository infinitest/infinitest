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

import java.io.*;
import java.util.*;

import org.infinitest.changedetect.*;
import org.junit.*;

public class WhenAnErrorOccurs {
	private ChangeDetector failingDetector;
	protected boolean shouldFail;
	private InfinitestCore core;

	@Before
	public void inContext() {
		shouldFail = true;
		failingDetector = new FakeChangeDetector() {
			@Override
			public Set<File> findChangedFiles() throws IOException {
				if (shouldFail) {
					throw new IOException();
				}
				return Collections.emptySet();
			}
		};
		core = createCore(failingDetector, withNoTestsToRun());
	}

	@Test
	public void shouldReloadIndexOnIOException() throws Exception {
		EventSupport statusSupport = new EventSupport();
		core.addTestQueueListener(statusSupport);
		core.update();
		shouldFail = false;
		core.update();
		shouldFail = true;
		core.update();
		statusSupport.assertReloadOccured();
	}

	@Test(expected = FatalInfinitestError.class)
	public void shouldNotRetryIfErrorOccursTwiceInARow() {
		core.update();
		core.update();
	}
}
