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
package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.*;
import static org.infinitest.eclipse.util.StatusMatchers.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.junit.Before;
import org.junit.Test;

// DEBT Duplication with WhenUpdatingTheProjectsInTheWorkspace
public class WhenCompileErrorsExistInAProject extends ResourceEventSupport
{
    private List<ProjectFacade> projects;
    private CoreRegistry coreRegistry;
    private ProjectSet projectSet;
    private EclipseWorkspace workspace;

    @Before
    public void inContext() throws CoreException
    {
        projects = newArrayList();
        projectSet = mock(ProjectSet.class);
        projects.add(new ProjectFacade(project));
        coreRegistry = mock(CoreRegistry.class);
        CoreFactory coreFactory = new CoreFactory(null);
        workspace = new EclipseWorkspace(projectSet, coreRegistry, coreFactory);

        when(projectSet.projects()).thenReturn(projects);
        when(projectSet.hasErrors()).thenReturn(true);
    }

    @Test
    public void shouldNotUpdateIt() throws CoreException
    {
        IJavaProject project = mock(IJavaProject.class);
        projects.clear();
        projects.add(new ProjectFacade(project));

        workspace.updateProjects();

        assertStatusIs(workspaceErrors());

        verifyZeroInteractions(project);
    }

    private void assertStatusIs(WorkspaceStatus expectedStatus)
    {
        assertThat(workspace.getStatus(), equalsStatus(expectedStatus));
    }
}
