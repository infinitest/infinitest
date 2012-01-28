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
package org.infinitest.intellij;

import java.awt.*;

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

	public void writeLogMessage(String message) {
	}

	public void writeError(String message) {
	}

	public void addResultClickListener(ResultClickListener listener) {
	}
}
