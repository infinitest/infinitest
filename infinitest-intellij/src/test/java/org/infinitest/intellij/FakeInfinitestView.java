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
package org.infinitest.intellij;

import java.awt.*;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.tree.*;

import org.infinitest.intellij.plugin.swingui.*;

@SuppressWarnings("all")
public class FakeInfinitestView implements InfinitestView {
	public void setAngerBasedOnTime(long timeSinceGreen) {
	}

	public void setVisible(boolean b) {
	}

	public void setProgress(int progress) {
	}

	public void setProgressBarColor(Color yellow) {
	}

	public void setMaximumProgress(int maxProgress) {
	}

	public int getMaximumProgress() {
		return 0;
	}

	public void setCycleTime(String timeStamp) {
	}

	public void setCurrentTest(String testName) {
	}

	public void addAction(Action action) {
	}

	public void setResultsModel(TreeModel results) {
	}

	public void setStatusMessage(String string) {
	}

	public void writeLogMessage(Level level, String message) {
	}

	public void writeError(String message, Throwable throwable) {
	}

	public void addResultClickListener(ResultClickListener listener) {
	}
	
	@Override
	public void consoleOutputUpdate(String newText, OutputType outputType) {
	}
}
