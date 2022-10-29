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
package org.infinitest.intellij.plugin.swingui.harness;

import static org.infinitest.intellij.plugin.swingui.harness.SwingPanelHarness.displayPanel;

import java.util.logging.Level;

import javax.swing.JFrame;

import org.infinitest.intellij.idea.IdeaLogService;
import org.infinitest.intellij.idea.LogServiceState;
import org.infinitest.intellij.plugin.swingui.InfinitestLogPane;

public class LogPaneHarness extends JFrame {
	private static final long serialVersionUID = -1L;
	private static final Exception EXCEPTION = new Exception("Uh-oh!");

	public static void main(String args[]) {
		IdeaLogService logService = new IdeaLogService();
		logService.loadState(new LogServiceState());
		InfinitestLogPane logPane = new InfinitestLogPane(logService);

		displayPanel(logPane);

		logPane.writeMessage(Level.INFO, "Something interesting happened");
		logPane.writeMessage(Level.INFO, "Something else interesting happened");
		logPane.writeError("Oops!", EXCEPTION);
	}
}
