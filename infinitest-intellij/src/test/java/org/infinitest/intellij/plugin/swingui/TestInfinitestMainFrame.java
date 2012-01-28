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
package org.infinitest.intellij.plugin.swingui;

import static org.infinitest.intellij.plugin.launcher.InfinitestPresenter.*;
import static org.junit.Assert.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.junit.*;

public class TestInfinitestMainFrame {
	private InfinitestMainFrame frame;

	@Before
	public void inContext() {
		frame = new InfinitestMainFrame();
	}

	@After
	public void cleanup() {
		frame = null;
	}

	@Test
	public void shouldDisplayProgress() {
		frame.setMaximumProgress(100);
		assertEquals(100, frame.getMaximumProgress());
		frame.setProgress(75);
	}

	@Test
	public void shouldUseUnknownColorToStart() {
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

			public void actionPerformed(ActionEvent e) {
				frame.setProgress(test.frame.getProgress() + 5);
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("Anger++") {
			private static final long serialVersionUID = -1L;

			public void actionPerformed(ActionEvent e) {
				frame.setAngerLevel(frame.getAngerLevel() + 1);
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("%--") {
			private static final long serialVersionUID = -1L;

			public void actionPerformed(ActionEvent e) {
				frame.setProgress(test.frame.getProgress() - 5);
			}
		}));

		controlPanel.add(new JButton(new AbstractAction("Anger--") {
			private static final long serialVersionUID = -1L;

			public void actionPerformed(ActionEvent e) {
				frame.setAngerLevel(frame.getAngerLevel() - 1);
			}
		}));

		System.out.println(UIManager.getSystemLookAndFeelClassName());
		frame.add(controlPanel, BorderLayout.NORTH);
		frame.setVisible(true);
	}
}
