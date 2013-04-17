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

	@Override
	public void registerToolWindow(JPanel panel, String windowId) {
		panel.setBackground(UIUtil.getTreeTextBackground());

		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		ToolWindow window = toolWindowManager.registerToolWindow(windowId, false, ToolWindowAnchor.BOTTOM);

		IdeaWindowHelper windowHelper = new IdeaWindowHelper();
		windowHelper.addPanelToWindow(panel, window);
	}

	@Override
	public void unregisterToolWindow(String windowId) {
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		toolWindowManager.unregisterToolWindow(windowId);
	}
}
