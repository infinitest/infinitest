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

import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.FAILING_COLOR;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.PASSING_COLOR;
import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.UNKNOWN_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.infinitest.intellij.IntellijMockBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestInfinitestMainFrame {
	private InfinitestMainFrame frame;

	@BeforeEach
	void inContext() {
		IntellijMockBase.setupApplication();
		frame = new InfinitestMainFrame(null);
	}

	@Test
	void shouldDisplayProgress() {
		frame.setMaximumProgress(100);
		assertEquals(100, frame.getMaximumProgress());
		frame.setProgress(75);
	}

	@Test
	void shouldUseUnknownColorToStart() {
		assertEquals(UNKNOWN_COLOR, frame.getProgressBarColor());
	}

	/**
	 * Test harness to make sure the frame looks pretty.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {
		final TestInfinitestMainFrame test = new TestInfinitestMainFrame();
		test.inContext();

		final InfinitestMainFrame frame = test.frame;
		frame.setProgressBarColor(FAILING_COLOR);
		frame.setMaximumProgress(100);
		JPanel controlPanel = new JPanel(new FlowLayout());

		controlPanel.add(new JButton(new AbstractAction("Color") {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getProgressBarColor().equals(PASSING_COLOR)) {
					frame.setProgressBarColor(FAILING_COLOR);
				} else {
					frame.setProgressBarColor(PASSING_COLOR);
				}
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("%++") {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setProgress(test.frame.getProgress() + 5);
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("Anger++") {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setAngerLevel(frame.getAngerLevel() + 1);
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("%--") {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setProgress(test.frame.getProgress() - 5);
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("Anger--") {
			private static final long serialVersionUID = -1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setAngerLevel(frame.getAngerLevel() - 1);
			}
		}));

		System.out.println(UIManager.getSystemLookAndFeelClassName());
		frame.add(controlPanel, BorderLayout.NORTH);
		frame.setVisible(true);
	}
}
