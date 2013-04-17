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
package org.infinitest.intellij.plugin.swingui;

import java.awt.event.*;

import javax.swing.*;

import org.infinitest.*;

public class HaltTestAction extends AbstractAction {
	private static final long serialVersionUID = -1L;

	private final ImageIcon stopIcon;
	private final ImageIcon startIcon;
	private final TestControl control;

	public HaltTestAction(TestControl testControl) {
		control = testControl;
		stopIcon = new ImageIcon(HaltTestAction.class.getResource("stop.png"));
		startIcon = new ImageIcon(HaltTestAction.class.getResource("rerun.png"));
		setIcon();
	}

	private void setIcon() {
		if (control.shouldRunTests()) {
			putValue(Action.SMALL_ICON, stopIcon);
			putValue(Action.SHORT_DESCRIPTION, "Stop running the tests");
		} else {
			putValue(Action.SMALL_ICON, startIcon);
			putValue(Action.SHORT_DESCRIPTION, "Start running the tests");
		}
	}

	public void actionPerformed(ActionEvent e) {
		control.setRunTests(!control.shouldRunTests());
		setIcon();
	}
}
