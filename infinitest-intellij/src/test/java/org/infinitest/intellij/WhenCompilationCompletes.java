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
