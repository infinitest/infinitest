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

import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;

public class IdeaWindowHelper {
	public static final String WAITING_ICON_PATH = "/infinitest-waiting.png";
	public static final String RUNNING_ICON_PATH = "/infinitest.png";
	public static final String SUCCESS_ICON_PATH = "/infinitest-success.png";
	public static final String FAILURE_ICON_PATH = "/infinitest-failure.png";

	@SuppressWarnings("deprecation")
	public void addPanelToWindow(JPanel rootPanel, ToolWindow window) {
		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content = contentFactory.createContent(rootPanel, "Infinitest", false);
		window.getContentManager().addContent(content);
		window.setIcon(IconLoader.getIcon(WAITING_ICON_PATH));
	}
}
