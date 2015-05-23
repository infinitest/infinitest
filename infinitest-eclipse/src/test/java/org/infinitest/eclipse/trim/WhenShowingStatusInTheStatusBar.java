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
package org.infinitest.eclipse.trim;

import static java.util.Arrays.*;
import static org.eclipse.swt.SWT.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.swt.*;
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

		verify(statusBar).setBackgroundColor(ColorSettings.getFailBackgroundColor());
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
	public void shouldChangeToFailBackgroundColorWhenTestsFail() {
		ColorSettings.setFailBackgroundColor(SWT.COLOR_DARK_RED);

		presenter.coreStatusChanged(PASSING, FAILING);

		verify(statusBar).setBackgroundColor(ColorSettings.getFailBackgroundColor());
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
