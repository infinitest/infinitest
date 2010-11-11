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
package org.infinitest.status;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.junit.Assert.*;

import org.infinitest.eclipse.status.WorkspaceStatus;
import org.junit.Test;

public class WhenUpdatingTheWorkspaceStatus
{
    @Test
    public void shouldDisplayAMessageWhenErrorsArePresent()
    {
        WorkspaceStatus status = workspaceErrors();
        assertEquals("Infinitest disabled. Errors in workspace.", status.getMessage());
        assertEquals(status.getMessage(), status.getToolTip());
        assertTrue(status.warningMessage());
    }

    @Test
    public void shouldReportWhichTestsAreRunning()
    {
        WorkspaceStatus status = runningTests(10, "org.fakeco.MyCurrentTest");
        assertEquals("Running MyCurrentTest (9 remaining)", status.getMessage());
        assertEquals("Current test: org.fakeco.MyCurrentTest", status.getToolTip());
    }

    @Test
    public void shouldReportTheNumberOfTestsRun()
    {
        WorkspaceStatus status = testRunFinished(asList("Test1", "Test2"));
        assertThat(status.getMessage(), startsWith("2 test cases ran"));
        assertEquals("Tests Ran:\nTest1\nTest2", status.getToolTip());
    }

    @Test
    public void shouldReportWhenNoTestsCouldBeRun()
    {
        WorkspaceStatus status = noTestsRun();
        assertTrue(status.warningMessage());
        assertEquals("No related tests found for last change.", status.getMessage());
    }
}
