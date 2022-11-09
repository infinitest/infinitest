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

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.tree.TreeModel;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;

public class InfinitestMainFrame implements InfinitestView {
	private final InfinitestResultsPane resultsPane;
	private final InfinitestLogPane logPane;
	private final InfinitestConsoleFrame consoleFrame;

	public InfinitestMainFrame(Project project, Application application) {
		this(new InfinitestResultsPane(), new InfinitestLogPane(application), new InfinitestConsoleFrame(project));
	}

	InfinitestMainFrame(InfinitestResultsPane resultsPane, InfinitestLogPane logPane, InfinitestConsoleFrame consoleFrame) {
		this.resultsPane = resultsPane;
		this.logPane = logPane;
		this.consoleFrame = consoleFrame;
	}

	public InfinitestResultsPane getResultsPane() {
		return resultsPane;
	}

	public InfinitestLogPane getLogPane() {
		return logPane;
	}

	public InfinitestConsoleFrame getConsoleFrame() {
		return consoleFrame;
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
	public void writeLogMessage(Level level, String message) {
		logPane.writeMessage(level, message);
	}

	@Override
	public void writeError(String message, Throwable throwable) {
		logPane.writeError(message, throwable);
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
	
	@Override
	public void consoleOutputUpdate(String newText, OutputType outputType) {
		consoleFrame.consoleOutputUpdate(newText, outputType);
	}
}
