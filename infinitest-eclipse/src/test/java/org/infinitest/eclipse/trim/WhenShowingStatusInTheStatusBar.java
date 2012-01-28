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
package org.infinitest.eclipse.trim;

import static java.util.Arrays.*;
import static org.eclipse.swt.SWT.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.infinitest.*;
import org.infinitest.eclipse.status.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenShowingStatusInTheStatusBar {
	private VisualStatusPresenter presenter;
	private VisualStatus statusBar;
	private TestQueueEvent firstEvent;

	@Before
	public void inContext() {
		statusBar = mock(VisualStatus.class);
		presenter = new VisualStatusPresenter();
		presenter.updateVisualStatus(statusBar);
		firstEvent = new TestQueueEvent(asList("ATest"), 2);
	}

	@Test
	public void shouldDisplayTheNumberOfRunningTests() {
		WorkspaceStatus expectedStatus = runningTests(1, "ATest");

		presenter.testQueueUpdated(firstEvent);

		verify(statusBar).setText(expectedStatus.getMessage());
		verify(statusBar).setToolTip(expectedStatus.getToolTip());
	}

	@Test
	public void shouldIgnoreUpdatesWhenStatusBarIsNotAttached() {
		presenter.statusChanged(workspaceErrors());
	}

	@Test
	public void shouldImmediatelySetStatusToFailingWhenATestFails() {
		presenter.testCaseComplete(new TestCaseEvent("", null, new TestResults(methodFailed("", "", new AssertionError()))));

		verify(statusBar).setBackgroundColor(COLOR_DARK_RED);
		verify(statusBar).setTextColor(COLOR_WHITE);
	}

	@Test
	public void shouldOnlyResetTestCounterWhenUpdateStarts() {
		presenter.testCaseComplete(testFinished("ATest"));
		presenter.testQueueUpdated(emptyQueueEvent(2));
		presenter.testCaseComplete(testFinished("BTest"));
		presenter.testCaseComplete(testFinished("CTest"));
		presenter.testQueueUpdated(emptyQueueEvent(2));
		presenter.filesSaved();
		presenter.testCaseComplete(testFinished("DTest"));
		presenter.testQueueUpdated(emptyQueueEvent(2));

		verify(statusBar, times(2)).setText(startsWith("1 test cases ran at "));
		verify(statusBar).setText(startsWith("3 test cases ran at "));
	}

	@Test
	public void shouldListTestsRunAsATooltip() {
		presenter.testCaseComplete(testFinished("ATest"));
		presenter.testQueueUpdated(emptyQueueEvent(1));

		verify(statusBar).setText(startsWith("1 test cases ran at "));
		verify(statusBar).setToolTip("Tests Ran:\nATest");
	}

	@Test
	public void shouldChangeToGreenWhenTestsPass() {
		presenter.coreStatusChanged(FAILING, PASSING);

		verify(statusBar).setBackgroundColor(COLOR_DARK_GREEN);
		verify(statusBar).setTextColor(COLOR_WHITE);
	}

	@Test
	public void shouldChangeToRedWhenTestsFail() {
		presenter.coreStatusChanged(PASSING, FAILING);

		verify(statusBar).setBackgroundColor(COLOR_DARK_RED);
		verify(statusBar).setTextColor(COLOR_WHITE);
	}

	@Test
	public void shouldChangeToYellowWhenStatusIsAWarning() {
		WorkspaceStatus warning = workspaceErrors();
		presenter.statusChanged(warning);

		verify(statusBar).setBackgroundColor(COLOR_YELLOW);
		verify(statusBar).setTextColor(COLOR_BLACK);
		verify(statusBar).setText(warning.getMessage());
		verify(statusBar).setToolTip(warning.getToolTip());
	}

	@Test
	public void shouldReportWhenAllTestsAreComplete() {
		presenter.testCaseComplete(testFinished("Test1"));
		presenter.testCaseComplete(testFinished("Test2"));
		presenter.testQueueUpdated(emptyQueueEvent(2));

		verify(statusBar).setText(startsWith("2 test cases ran at "));
	}

	private TestQueueEvent emptyQueueEvent(int initialSize) {
		return new TestQueueEvent(Collections.<String> emptyList(), initialSize);
	}

	private TestCaseEvent testFinished(String testName) {
		return new TestCaseEvent(testName, this, new TestResults());
	}
}
