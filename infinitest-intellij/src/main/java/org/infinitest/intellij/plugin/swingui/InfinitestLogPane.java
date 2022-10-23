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

import static java.awt.BorderLayout.*;
import static java.lang.String.format;

import java.awt.*;
import java.io.*;
import java.util.logging.Level;

import javax.swing.*;

public class InfinitestLogPane extends JPanel {
	private static final long serialVersionUID = -1L;
	private static final String CRLF = System.getProperty("line.separator");

	private final JTextArea textArea;
	private final JComboBox<Level> levelComboBox;
	
	public InfinitestLogPane() {
		super(new BorderLayout());
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		setLayout(new BorderLayout());
		add(new JScrollPane(textArea), CENTER);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
	
		levelComboBox = new JComboBox<>(logLevels());
		toolBar.add(levelComboBox);
		
		add(toolBar, BorderLayout.SOUTH);
	}

	private Level[] logLevels() {
		return new Level[] {Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINEST, Level.ALL};
	}

	public void writeMessage(final Level level, final String message) {
		Level currentLevel = (Level) levelComboBox.getSelectedItem();

		if (level.intValue() >= currentLevel.intValue()) {
			SwingUtilities.invokeLater(() -> {
				textArea.append(leftAlign(level) + " " + message);
				textArea.append(CRLF);
			});
		}
	}

	public void writeError(String message, Throwable error) {
		writeMessage(Level.SEVERE, message);
		writeMessage(Level.SEVERE, stackTraceFor(error));
	}

	private String stackTraceFor(Throwable error) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output);
		error.printStackTrace(writer);
		writer.close();

		return output.toString();
	}

	private String leftAlign(Level info) {
		return format("%-10s", info.getName());
	}
}
