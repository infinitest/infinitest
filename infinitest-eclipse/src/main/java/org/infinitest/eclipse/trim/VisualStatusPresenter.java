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

import static com.google.common.collect.Sets.*;
import static org.eclipse.swt.SWT.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;

import java.util.Set;

import org.infinitest.CoreStatus;
import org.infinitest.TestQueueAdapter;
import org.infinitest.TestQueueEvent;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.springframework.stereotype.Component;

@Component
public class VisualStatusPresenter extends TestQueueAdapter implements VisualStatusRegistry
{
    private VisualStatus status;
    private final Set<String> testsRan = newLinkedHashSet();

    @Override
    public void testQueueUpdated(TestQueueEvent event)
    {
        if (!event.getTestQueue().isEmpty())
        {
            statusChanged(runningTests(event.getTestQueue().size(), event.getCurrentTest()));
        }
        else
        {
            statusChanged(testRunFinished(testsRan));
        }
    }

    public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        switch (newStatus)
        {
        case PASSING:
            status.setBackgroundColor(COLOR_DARK_GREEN);
            status.setTextColor(COLOR_WHITE);
            break;
        case FAILING:
            setFailingColors();
            break;
        default:
            break;
        }
    }

    private void setFailingColors()
    {
        status.setBackgroundColor(COLOR_DARK_RED);
        status.setTextColor(COLOR_WHITE);
    }

    public void updateVisualStatus(VisualStatus status)
    {
        this.status = status;
    }

    public void filesSaved()
    {
        testsRan.clear();
    }

    public void testCaseComplete(TestCaseEvent event)
    {
        testsRan.add(event.getTestName());
        if (event.failed())
        {
            setFailingColors();
            // Remove this test from the set of tests currently being run.
        }

        // Keep in mind that tests can be interrupted before they are completed,
        // so testCaseStarting will be called again before this is called
    }

    public void testCaseStarting(TestEvent event)
    {
        // Add this test to the set of tests currently being run
    }

    public void statusChanged(WorkspaceStatus newStatus)
    {
        status.setText(newStatus.getMessage());
        status.setToolTip(newStatus.getToolTip());
        if (newStatus.warningMessage())
        {
            status.setBackgroundColor(COLOR_YELLOW);
            status.setTextColor(COLOR_BLACK);
        }
    }
}