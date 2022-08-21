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

import javax.swing.Action;
import javax.swing.tree.TreeModel;

import org.infinitest.ConsoleOutputListener;

/**
 * @author bjrady
 */
public interface InfinitestView extends ConsoleOutputListener {
	void setAngerBasedOnTime(long timeSinceGreen);

	void setVisible(boolean b);

	void setProgress(int progress);

	void setProgressBarColor(Color yellow);

	void setMaximumProgress(int maxProgress);

	int getMaximumProgress();

	void setCycleTime(String timeStamp);

	void setCurrentTest(String testName);

	void addAction(Action action);

	void setResultsModel(TreeModel results);

	void setStatusMessage(String string);

	void writeLogMessage(String message);

	void writeError(String message);

	void addResultClickListener(ResultClickListener listener);
}
