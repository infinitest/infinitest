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
package org.infinitest.intellij.idea.greenhook;

import static org.infinitest.CoreStatus.*;
import static org.mockito.Mockito.*;

import org.infinitest.intellij.plugin.greenhook.*;
import org.junit.*;

public class WhenTestRunCompletes {
	private GreenHookListener listener;
	private GreenHook hook;

	@Before
	public void inContext() {
		listener = new GreenHookListener();
		hook = mock(GreenHook.class);
		listener.add(hook);
	}

	@Test
	public void shouldRunGreenHooksIfPassing() {
		listener.coreStatusChanged(PASSING, PASSING);
		verify(hook).execute();
	}

	@Test
	public void shouldNotRunGreenHooksIfFailing() {
		listener.coreStatusChanged(PASSING, FAILING);
		verify(hook, never()).execute();
	}

	@Test
	public void shouldNotThrowExceptionIfNoGreenHooksAdded() {
		listener = new GreenHookListener();
		listener.coreStatusChanged(PASSING, PASSING);
		verify(hook, never()).execute();
	}
}
