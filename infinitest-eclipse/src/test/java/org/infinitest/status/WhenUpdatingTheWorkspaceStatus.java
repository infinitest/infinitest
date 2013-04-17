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
package org.infinitest.status;

import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.junit.Assert.*;

import org.infinitest.eclipse.status.*;
import org.junit.*;

public class WhenUpdatingTheWorkspaceStatus {
	@Test
	public void shouldDisplayAMessageWhenErrorsArePresent() {
		WorkspaceStatus status = workspaceErrors();
		assertEquals("Infinitest disabled. Errors in workspace.", status.getMessage());
		assertEquals(status.getMessage(), status.getToolTip());
		assertTrue(status.warningMessage());
	}

	@Test
	public void shouldReportWhichTestsAreRunning() {
		WorkspaceStatus status = runningTests(10, "org.fakeco.MyCurrentTest");
		assertEquals("Running MyCurrentTest (9 remaining)", status.getMessage());
		assertEquals("Current test: org.fakeco.MyCurrentTest", status.getToolTip());
	}

	@Test
	public void shouldReportTheNumberOfTestsRun() {
		WorkspaceStatus status = testRunFinished(asList("Test1", "Test2"));
		assertThat(status.getMessage(), startsWith("2 test cases ran"));
		assertEquals("Tests Ran:\nTest1\nTest2", status.getToolTip());
	}

	@Test
	public void shouldReportWhenNoTestsCouldBeRun() {
		WorkspaceStatus status = noTestsRun();
		assertTrue(status.warningMessage());
		assertEquals("No related tests found for last change.", status.getMessage());
	}
}
