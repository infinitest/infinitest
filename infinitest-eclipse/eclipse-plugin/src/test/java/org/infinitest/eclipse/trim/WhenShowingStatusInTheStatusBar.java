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
import static org.easymock.EasyMock.*;
import static org.eclipse.swt.SWT.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;

import java.util.Collections;

import org.infinitest.EventSupport;
import org.infinitest.TestQueueEvent;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

public class WhenShowingStatusInTheStatusBar
{
    private VisualStatusPresenter presenter;
    private VisualStatus statusBar;
    private TestQueueEvent firstEvent;

    @Before
    public void inContext()
    {
        statusBar = createMock(VisualStatus.class);
        presenter = new VisualStatusPresenter();
        presenter.updateVisualStatus(statusBar);
        firstEvent = new TestQueueEvent(asList("ATest"), 2);
    }

    @Test
    public void shouldDisplayTheNumberOfRunningTests()
    {
        WorkspaceStatus expectedStatus = runningTests(1, "ATest");
        statusBar.setText(expectedStatus.getMessage());
        statusBar.setToolTip(expectedStatus.getToolTip());
        replay(statusBar);
        presenter.testQueueUpdated(firstEvent);
        verify(statusBar);
    }

    @Test
    public void shouldIgnoreUpdatesWhenStatusBarIsNotAttached()
    {
        presenter.statusChanged(workspaceErrors());
    }

    @Test
    public void shouldImmediatelySetStatusToFailingWhenATestFails()
    {
        expectFailingColors();
        replay(statusBar);

        presenter.testCaseComplete(EventSupport.testCaseFailing("Test", "method", new AssertionError()));
        verify(statusBar);
    }

    @Test
    public void shouldOnlyResetTestCounterWhenUpdateStarts()
    {
        statusBar.setToolTip((String) anyObject());
        expectLastCall().anyTimes();
        statusBar.setText(startsWith("1 test cases ran at "));
        statusBar.setText(startsWith("3 test cases ran at "));
        statusBar.setText(startsWith("1 test cases ran at "));
        replay(statusBar);
        presenter.testCaseComplete(testFinished("ATest"));
        presenter.testQueueUpdated(emptyQueueEvent(2));
        presenter.testCaseComplete(testFinished("BTest"));
        presenter.testCaseComplete(testFinished("CTest"));
        presenter.testQueueUpdated(emptyQueueEvent(2));
        presenter.filesSaved();
        presenter.testCaseComplete(testFinished("DTest"));
        presenter.testQueueUpdated(emptyQueueEvent(2));

        verify(statusBar);
    }

    @Test
    public void shouldListTestsRunAsATooltip()
    {
        statusBar.setText(startsWith("1 test cases ran at "));
        statusBar.setToolTip("Tests Ran:\nATest");
        replay(statusBar);
        presenter.testCaseComplete(testFinished("ATest"));
        presenter.testQueueUpdated(emptyQueueEvent(1));
        verify(statusBar);
    }

    @Test
    public void shouldChangeToGreenWhenTestsPass()
    {
        statusBar.setBackgroundColor(COLOR_DARK_GREEN);
        statusBar.setTextColor(COLOR_WHITE);
        replay(statusBar);
        presenter.coreStatusChanged(FAILING, PASSING);
        verify(statusBar);
    }

    @Test
    public void shouldChangeToRedWhenTestsFail()
    {
        expectFailingColors();
        replay(statusBar);
        presenter.coreStatusChanged(PASSING, FAILING);
        verify(statusBar);
    }

    private void expectFailingColors()
    {
        statusBar.setBackgroundColor(COLOR_DARK_RED);
        statusBar.setTextColor(COLOR_WHITE);
    }

    @Test
    public void shouldChangeToYellowWhenStatusIsAWarning()
    {
        WorkspaceStatus warning = workspaceErrors();
        statusBar.setBackgroundColor(COLOR_YELLOW);
        statusBar.setTextColor(COLOR_BLACK);
        statusBar.setText(warning.getMessage());
        statusBar.setToolTip(warning.getToolTip());
        replay(statusBar);
        presenter.statusChanged(warning);
        verify(statusBar);
    }

    @Test
    public void shouldReportWhenAllTestsAreComplete()
    {
        statusBar.setToolTip((String) anyObject());
        expectLastCall().anyTimes();
        statusBar.setText(startsWith("2 test cases ran at "));
        replay(statusBar);
        presenter.testCaseComplete(testFinished("Test1"));
        presenter.testCaseComplete(testFinished("Test2"));
        presenter.testQueueUpdated(emptyQueueEvent(2));
        verify(statusBar);
    }

    private TestQueueEvent emptyQueueEvent(int initialSize)
    {
        return new TestQueueEvent(Collections.<String> emptyList(), initialSize);
    }

    private TestCaseEvent testFinished(String testName)
    {
        return new TestCaseEvent(testName, this, new TestResults());
    }
}
