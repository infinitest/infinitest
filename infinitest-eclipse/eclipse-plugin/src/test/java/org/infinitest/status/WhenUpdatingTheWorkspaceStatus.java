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
