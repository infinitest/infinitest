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
package org.infinitest.intellij;

import static org.mockito.Mockito.*;

import org.infinitest.*;
import org.infinitest.intellij.idea.*;
import org.junit.*;
import org.mockito.*;

import com.intellij.openapi.compiler.*;

public class WhenCompilationCompletes {
	private final ModuleSettings moduleSettings = new FakeModuleSettings("test");
	private InfinitestCore core;

	@Before
	public void inContext() {
		core = mock(InfinitestCore.class);
	}

	@Test
	public void shouldInvokeUpdateOnCore() {
		CompilationStatusListener listener = new IdeaCompilationListener(core, moduleSettings);
		listener.compilationFinished(false, 0, 0, null);

		verify(core).setRuntimeEnvironment(Matchers.any(RuntimeEnvironment.class));
		verify(core).update();
	}

	@Test
	public void shouldNotUpdateIfCompilationAborted() {
		CompilationStatusListener listener = new IdeaCompilationListener(core, moduleSettings);
		listener.compilationFinished(true, 0, 0, null);

		verify(core, never()).setRuntimeEnvironment(Matchers.any(RuntimeEnvironment.class));
		verify(core, never()).update();
	}

	@Test
	public void shouldNotUpdateIfCompileErrorsOccurred() {
		CompilationStatusListener listener = new IdeaCompilationListener(core, moduleSettings);
		listener.compilationFinished(false, 1, 0, null);

		verify(core, never()).setRuntimeEnvironment(Matchers.any(RuntimeEnvironment.class));
		verify(core, never()).update();
	}
}
