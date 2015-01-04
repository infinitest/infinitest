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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import javax.swing.*;

import org.infinitest.intellij.plugin.launcher.*;
import org.junit.*;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.wm.*;

public class WhenLaunchingInfinitest {
	private ModuleSettings moduleSettings = new FakeModuleSettings("foo");

	private ToolWindowRegistry registry = mock(ToolWindowRegistry.class);
	private FileEditorManager fileEditorManagerMock = mock(FileEditorManager.class);
	private ToolWindowManager toolWindowManagerMock = mock(ToolWindowManager.class);

	@Test
	public void shouldNameToolWindowAfterModule() {
		InfinitestLauncher launcher = new InfinitestLauncher(moduleSettings, registry, new FakeCompilationNotifier(), new FakeSourceNavigator(), fileEditorManagerMock, toolWindowManagerMock);
		launcher.launchInfinitest();

		verify(registry).registerToolWindow(any(JPanel.class), eq("foo"));
	}
}
