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

import static com.google.common.collect.Lists.*;
import static org.infinitest.eclipse.util.StatusMatchers.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.status.*;
import org.junit.*;

// DEBT Duplication with WhenUpdatingTheProjectsInTheWorkspace
public class WhenCompileErrorsExistInAProject extends ResourceEventSupport {
	private List<ProjectFacade> projects;
	private CoreRegistry coreRegistry;
	private ProjectSet projectSet;
	private EclipseWorkspace workspace;

	@Before
	public void inContext() throws CoreException {
		projects = newArrayList();
		projectSet = mock(ProjectSet.class);
		projects.add(new ProjectFacade(project));
		coreRegistry = mock(CoreRegistry.class);
		CoreFactory coreFactory = new CoreFactory(null);
		workspace = new EclipseWorkspace(projectSet, coreRegistry, coreFactory, new SystemClassPathJarLocator());

		when(projectSet.projects()).thenReturn(projects);
		when(projectSet.hasErrors()).thenReturn(true);
	}

	@Test
	public void shouldNotUpdateIt() throws CoreException {
		IJavaProject project = mock(IJavaProject.class);
		projects.clear();
		projects.add(new ProjectFacade(project));

		workspace.updateProjects();

		assertStatusIs(workspaceErrors());

		verifyZeroInteractions(project);
	}

	private void assertStatusIs(WorkspaceStatus expectedStatus) {
		assertThat(workspace.getStatus(), equalsStatus(expectedStatus));
	}
}
