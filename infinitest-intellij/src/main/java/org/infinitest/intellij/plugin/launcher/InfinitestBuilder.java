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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.infinitest.*;
import org.infinitest.intellij.idea.language.*;
import org.infinitest.intellij.plugin.swingui.*;

public class InfinitestBuilder {
	private ResultCollector resultCollector;
	private InfinitestPresenter presenter;
	private InfinitestView view;
	private final InfinitestCore core;
	private TestControl testControl;

	public InfinitestBuilder(InfinitestCore core) {
		this.core = core;
		resultCollector = new ResultCollector(core);
	}

	public JPanel createPluginComponent(TestControl testControl) {
		this.testControl = testControl;
		JFrame frame = (JFrame) getView();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(frame.getContentPane(), BorderLayout.CENTER);

		createPresenter();

		return panel;
	}

	public InfinitestCore getCore() {
		return core;
	}

	public InfinitestView getView() {
		if (view == null) {
			view = InfinitestMainFrame.createFrame(resultCollector);
		}
		return view;
	}

	private InfinitestPresenter createPresenter() {
		if (presenter == null) {
			presenter = new InfinitestPresenter(resultCollector, core, getView(), testControl, IdeaInfinitestAnnotator.getInstance());
		}
		return presenter;
	}

	public void startApp() {
		createPresenter();
		view.setVisible(true);
		createStatusTimer();
	}

	private void createStatusTimer() {
		javax.swing.Timer statusTimer = new javax.swing.Timer(999, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPresenter().updateStatus();
			}
		});
		statusTimer.start();
	}

	public void setResultCollector(ResultCollector resultCollector) {
		this.resultCollector = resultCollector;
	}

	public void addStatusListener(StatusChangeListener listener) {
		resultCollector.addStatusChangeListener(listener);
	}

	public void addResultClickListener(ResultClickListener listener) {
		view.addResultClickListener(listener);
	}

	public void removeStatusListener(StatusChangeListener listener) {
		resultCollector.removeStatusChangeListener(listener);
	}

	public void addPresenterListener(PresenterListener listener) {
		presenter.addPresenterListener(listener);
	}
}
