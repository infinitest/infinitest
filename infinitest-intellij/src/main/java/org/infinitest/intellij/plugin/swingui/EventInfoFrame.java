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

import static org.infinitest.util.InfinitestUtils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.infinitest.testrunner.*;

class EventInfoFrame extends JDialog {
	private static final long serialVersionUID = -1L;

	private final JTextArea textArea;
	private final JButton closeButton;

	public EventInfoFrame(TestEvent event) {
		setAlwaysOnTop(true);
		textArea = new JTextArea(stackTraceToString(event.getStackTrace()));
		textArea.setEditable(false);
		textArea.setColumns(80);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		getContentPane().add(new JScrollPane(textArea));
		Container southPanel = new JPanel(new FlowLayout());
		Action disposeAction = new AbstractAction("Close") {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		closeButton = new JButton(disposeAction);
		southPanel.add(closeButton);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		String message = event.getMessage();
		if (message == null) {
			message = "";
		}
		setTitle(event.getTestName() + "." + event.getTestMethod() + "()");
		pack();
		closeButton.requestFocusInWindow();
	}

	public JButton getCloseButton() {
		return closeButton;
	}

	public static String stackTraceToString(StackTraceElement[] stackTrace) {
		if (stackTrace == null) {
			return "";
		}
		return listToMultilineString(Arrays.asList(stackTrace));
	}

	@Override
	protected JRootPane createRootPane() {
		JRootPane root = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		};
		InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		root.getActionMap().put("ESCAPE", actionListener);

		return root;
	}

	public JTextArea getTextArea() {
		return textArea;
	}
}
