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

import java.awt.*;
import java.net.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.infinitest.*;

public class InfinitestMainFrame extends JFrame implements InfinitestView {
	private static final long serialVersionUID = -1L;
	private static final String APP_TITLE = "Infinitest";

	private final InfinitestResultsPane resultsPane;
	private final InfinitestLogPane logPane;

	public InfinitestMainFrame() {
		this(new InfinitestResultsPane(), new InfinitestLogPane());
	}

	InfinitestMainFrame(InfinitestResultsPane resultsPane, InfinitestLogPane logPane) {
		this.resultsPane = resultsPane;
		this.logPane = logPane;
		initializeFrame();

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Results", resultsPane);
		tabbedPane.add("Logging", logPane);
		add(tabbedPane);
	}

	public static InfinitestView createFrame(ResultCollector results) {
		InfinitestMainFrame mainFrame = new InfinitestMainFrame();
		mainFrame.setResultsModel(new TreeModelAdapter(results));
		return mainFrame;
	}

	private void initializeFrame() {
		URL iconURL = InfinitestMainFrame.class.getResource("infinitest-icon.png");
		setIconImage(new ImageIcon(iconURL).getImage());
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowFocusListener(new TreeFocusListener());
	}

	@Override
	public void setAngerBasedOnTime(long timeSinceGreen) {
		resultsPane.setAngerBasedOnTime(timeSinceGreen);
	}

	@Override
	public void setProgress(int progress) {
		resultsPane.setProgress(progress);
	}

	public int getProgress() {
		return resultsPane.getProgress();
	}

	@Override
	public void setProgressBarColor(Color color) {
		resultsPane.setProgressBarColor(color);
	}

	public Color getProgressBarColor() {
		return resultsPane.getProgressBarColor();
	}

	@Override
	public void setMaximumProgress(int maxProgress) {
		resultsPane.setMaximumProgress(maxProgress);
	}

	@Override
	public int getMaximumProgress() {
		return resultsPane.getMaximumProgress();
	}

	@Override
	public void setCycleTime(String timeStamp) {
		setTitle(APP_TITLE + " - Cycle Time: " + timeStamp);
	}

	@Override
	public void setCurrentTest(String testName) {
		resultsPane.setCurrentTest(testName);
	}

	@Override
	public void addAction(Action action) {
		resultsPane.addAction(action);
	}

	@Override
	public void setResultsModel(TreeModel results) {
		resultsPane.setResultsModel(results);
	}

	@Override
	public void setStatusMessage(String message) {
		resultsPane.setStatusMessage(message);
	}

	@Override
	public void writeLogMessage(String message) {
		logPane.writeMessage(message);
	}

	@Override
	public void writeError(String message) {
		// nothing to do here
	}

	@Override
	public void addResultClickListener(ResultClickListener listener) {
		resultsPane.addResultClickListener(listener);
	}

	public int getAngerLevel() {
		return resultsPane.getAngerLevel();
	}

	public void setAngerLevel(int anger) {
		resultsPane.setAngerLevel(anger);
	}

	public Component getTree() {
		return resultsPane.getTree();
	}
}
