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
package org.infinitest.intellij.plugin.launcher;

import javax.swing.*;

import org.infinitest.intellij.idea.window.*;

import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.*;

/**
 * Created by IntelliJ IDEA. User: aurelien Date: 09/06/11 Time: 21:26
 */
public class ToolWindowListener implements PresenterListener {
	ToolWindowManager toolWindowManager;
	String toolWindowId;

	public ToolWindowListener(ToolWindowManager toolWindowManager, String toolWindowId) {
		this.toolWindowManager = toolWindowManager;
		this.toolWindowId = toolWindowId;
	}

	@Override
	public void testRunCompleted() {
		// nothing to do here
	}

	@Override
	public void testRunSucceed() {
		editToolWindowIcon(IconLoader.getIcon(IdeaWindowHelper.SUCCESS_ICON_PATH));
	}

	@Override
	public void testRunFailed() {
		editToolWindowIcon(IconLoader.getIcon(IdeaWindowHelper.FAILURE_ICON_PATH));
	}

	@Override
	public void testRunStarted() {
		editToolWindowIcon(IconLoader.getIcon(IdeaWindowHelper.RUNNING_ICON_PATH));
	}

	@Override
	public void testRunWaiting() {
		editToolWindowIcon(IconLoader.getIcon(IdeaWindowHelper.WAITING_ICON_PATH));
	}

	private void editToolWindowIcon(Icon icon) {
		ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowId);
		if (toolWindow != null) {
			toolWindow.setIcon(icon);
		}
	}
}
