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
package org.infinitest.eclipse.workspace;

import static java.text.DateFormat.getTimeInstance;
import static org.infinitest.util.InfinitestUtils.listToMultilineString;
import static org.infinitest.util.InfinitestUtils.stripPackageName;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import org.infinitest.eclipse.status.WorkspaceStatus;

public class WorkspaceStatusFactory {
	public static WorkspaceStatus workspaceErrors() {
		return new WarningStatus("Infinitest disabled. Errors in workspace.");
	}

	public static WorkspaceStatus runningTests(int remainingTests, String currentTest) {
		String message = "Running " + stripPackageName(currentTest) + " (" + (remainingTests - 1) + " remaining)";
		return new TooltippedStatus(message, "Current test: " + currentTest);
	}

	public static WorkspaceStatus testRunFinished(Collection<String> testsRan) {
		String message = testsRan.size() + " test cases ran at " + timestamp();
		return new TooltippedStatus(message, "Tests Ran:\n" + listToMultilineString(testsRan));
	}

	public static WorkspaceStatus noTestsRun() {
		return new WarningStatus("No related tests found for last change.");
	}

	private static String timestamp() {
		return getTimeInstance(DateFormat.MEDIUM).format(new Date());
	}

	private static class SimpleStringStatus implements WorkspaceStatus {
		private final String message;

		public SimpleStringStatus(String message) {
			this.message = message;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public String getToolTip() {
			return getMessage();
		}

		@Override
		public boolean warningMessage() {
			return false;
		}
	}

	private static class WarningStatus extends SimpleStringStatus {
		public WarningStatus(String message) {
			super(message);
		}

		@Override
		public boolean warningMessage() {
			return true;
		}
	}

	private static class TooltippedStatus extends SimpleStringStatus {
		private final String tooltip;

		public TooltippedStatus(String message, String tooltip) {
			super(message);
			this.tooltip = tooltip;
		}

		@Override
		public String getToolTip() {
			return tooltip;
		}
	}

	public static WorkspaceStatus findingTests(int analyzedProjects, int totalProjects, int testsFoundSoFar) {		
		return new TooltippedStatus("Looking for tests: "+ testsFoundSoFar +" found so far", analyzedProjects + " out of "+totalProjects +" projects analyzed");
	}
}
