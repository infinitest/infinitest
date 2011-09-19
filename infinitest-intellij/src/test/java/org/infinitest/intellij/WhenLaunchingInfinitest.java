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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.swing.JPanel;

import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncherImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.wm.ToolWindowManager;

public class WhenLaunchingInfinitest
{
    private ModuleSettings moduleSettings;

    @Before
    public void setUp()
    {
        moduleSettings = new FakeModuleSettings("foo");
    }

    @Test
    public void shouldNameToolWindowAfterModule()
    {
        ToolWindowRegistry registry = mock(ToolWindowRegistry.class);
        FileEditorManager fileEditorManagerMock = mock(FileEditorManager.class);
        ToolWindowManager toolWindowManagerMock = mock(ToolWindowManager.class);

        InfinitestLauncher launcher = new InfinitestLauncherImpl(moduleSettings, registry,
                        new FakeCompilationNotifier(), new FakeSourceNavigator(), fileEditorManagerMock,
                        toolWindowManagerMock);
        launcher.launchInfinitest();

        verify(registry).registerToolWindow(Matchers.any(JPanel.class), eq("Infinitest_foo"));
    }
}
