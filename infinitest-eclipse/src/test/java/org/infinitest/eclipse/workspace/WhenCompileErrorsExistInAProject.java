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

import static org.infinitest.eclipse.util.StatusMatchers.equalsStatus;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.workspaceErrors;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.SystemClassPathJarLocator;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.util.InfinitestGlobalSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// DEBT Duplication with WhenUpdatingTheProjectsInTheWorkspace
class WhenCompileErrorsExistInAProject extends ResourceEventSupport {
	private List<ProjectFacade> projects;
	private IJavaProject projectMock;
	private CoreRegistry coreRegistry;
	private ProjectSet projectSet;
	private EclipseWorkspace workspace;
	private Set<IResource> modifiedResources;
	private IResource modifiedResource;

	@BeforeEach
	void inContext() throws CoreException {
		projects = new ArrayList<>();
		projectSet = mock(ProjectSet.class);
		projectMock = mock(IJavaProject.class);
		projects.add(new ProjectFacade(projectMock) {
			@Override
			public String rawClasspath() throws CoreException {
				return "";
			}
		});
		coreRegistry = mock(CoreRegistry.class);
		modifiedResource = mock(IResource.class);
		IPath modifiedResourcePath = mock(IPath.class);
		modifiedResources = Collections.singleton(modifiedResource);
		
		CoreFactory coreFactory = new CoreFactory(null);
		workspace = new EclipseWorkspace(projectSet, coreRegistry, coreFactory, new SystemClassPathJarLocator()) {
			@Override
			protected int updateProject(ProjectFacade project, java.util.Collection<java.io.File> changedFiles) throws CoreException {
				return 42;
			}
		};

		when(projectSet.projects()).thenReturn(projects);
		when(projectSet.hasErrors()).thenReturn(true);
		when(projectMock.isOnClasspath(modifiedResource)).thenReturn(true);
		
		when(modifiedResource.getRawLocation()).thenReturn(modifiedResourcePath);
		when(modifiedResourcePath.makeAbsolute()).thenReturn(modifiedResourcePath);
	}

	@Test
	void shouldNotUpdateWhenDisabled() throws CoreException {
		InfinitestGlobalSettings.setDisableWhenWorkspaceHasErrors(true);
		
		workspace.updateProjects(Collections.emptySet());

		assertStatusIs(workspaceErrors());

		verifyNoInteractions(projectMock);
	}

	@Test
	void shouldUpdateItWhenEnabled() throws CoreException {
		InfinitestGlobalSettings.setDisableWhenWorkspaceHasErrors(false);
		
		workspace.updateProjects(modifiedResources);

		verify(projectMock, times(1)).isOnClasspath(modifiedResource);
	}

	private void assertStatusIs(WorkspaceStatus expectedStatus) {
		assertThat(workspace.getStatus(), equalsStatus(expectedStatus));
	}
}
