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
package org.infinitest.intellij.idea.window;

import javax.swing.*;

import org.infinitest.intellij.*;

import com.intellij.openapi.project.*;
import com.intellij.openapi.wm.*;
import com.intellij.util.ui.*;

public class IdeaToolWindowRegistry implements ToolWindowRegistry {
	private final Project project;

	public IdeaToolWindowRegistry(Project project) {
		this.project = project;
	}

	public void registerToolWindow(JPanel panel, String windowId) {
		panel.setBackground(UIUtil.getTreeTextBackground());

		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		ToolWindow window = toolWindowManager.registerToolWindow(windowId, false, ToolWindowAnchor.BOTTOM);

		IdeaWindowHelper windowHelper = new IdeaWindowHelper();
		windowHelper.addPanelToWindow(panel, window);
	}

	public void unregisterToolWindow(String windowId) {
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		toolWindowManager.unregisterToolWindow(windowId);
	}
}
