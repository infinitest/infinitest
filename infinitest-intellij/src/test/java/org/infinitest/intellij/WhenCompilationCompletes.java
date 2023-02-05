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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.infinitest.InfinitestCore;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.intellij.idea.IdeaCompilationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intellij.task.ProjectTaskListener;
import com.intellij.task.ProjectTaskManager.Result;

class WhenCompilationCompletes extends IntellijMockBase {
	private final ModuleSettings moduleSettings = new FakeModuleSettings("test");
	private InfinitestCore core;
	private Result result;

	@BeforeEach
	void inContext() {
		setupApplication(false);
		
		core = mock(InfinitestCore.class);
		result = mock(Result.class);
		
		when(module.getService(ModuleSettings.class)).thenReturn(moduleSettings);
		when(launcher.getCore()).thenReturn(core);
	}

	@Test
	void shouldInvokeUpdateOnCore() {
		ProjectTaskListener listener = new IdeaCompilationListener(project);
		when(result.isAborted()).thenReturn(false);
		listener.finished(result);

		verify(core).setRuntimeEnvironment(any(RuntimeEnvironment.class));
		verify(core).update();
	}

	@Test
	void shouldNotUpdateIfCompilationAborted() {
		ProjectTaskListener listener = new IdeaCompilationListener(project);
		when(result.isAborted()).thenReturn(true);
		listener.finished(result);

		verify(core, never()).setRuntimeEnvironment(any(RuntimeEnvironment.class));
		verify(core, never()).update();
	}

	@Test
	void shouldNotUpdateIfCompileErrorsOccurred() {
		ProjectTaskListener listener = new IdeaCompilationListener(project);
		when(result.isAborted()).thenReturn(false);
		when(result.hasErrors()).thenReturn(true);
		listener.finished(result);

		verify(core, never()).setRuntimeEnvironment(any(RuntimeEnvironment.class));
		verify(core, never()).update();
	}
}
