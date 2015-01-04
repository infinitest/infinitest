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
import static java.util.Collections.*;
import static org.infinitest.eclipse.util.StatusMatchers.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.infinitest.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.status.*;
import org.junit.*;

public class WhenUpdatingTheProjectsInTheWorkspace extends ResourceEventSupport {
	private List<ProjectFacade> projects;
	private CoreRegistry coreRegistry;
	private ProjectSet projectSet;
	private EclipseWorkspace workspace;
	private WorkspaceStatus updatedStatus;
	private int updates;

	@Before
	public void inContext() throws CoreException {
		projects = newArrayList();
		projectSet = mock(ProjectSet.class);
		projects.add(newFacade(project));
		coreRegistry = mock(CoreRegistry.class);
		CoreFactory coreFactory = new CoreFactory(null);

		when(projectSet.projects()).thenReturn(projects);
		when(projectSet.hasErrors()).thenReturn(false);
		List<File> outputDirs = emptyList();
		when(projectSet.outputDirectories(any(EclipseProject.class))).thenReturn(outputDirs);

		workspace = new EclipseWorkspace(projectSet, coreRegistry, coreFactory);
	}

	private ProjectFacade newFacade(IJavaProject project) {
		return new ProjectFacade(project) {
			@Override
			public String rawClasspath() {
				return "classpath";
			}
		};
	}

	@Test
	public void shouldCreateACoreOnUpdateIfNoneExists() throws CoreException {
		URI projectAUri = projectAUri();
		when(coreRegistry.getCore(projectAUri)).thenReturn(null);

		workspace.updateProjects();

		assertStatusIs(noTestsRun());
		verify(coreRegistry).addCore(eq(projectAUri), any(InfinitestCore.class));
	}

	@Test
	public void shouldFireAnEvent() throws CoreException {
		InfinitestCore core = prepateCore(projectAUri(), 10);

		workspace.addUpdateListeners(new UpdateListener() {
			@Override
			public void projectsUpdated() {
				updates++;
			}
		});

		workspace.updateProjects();
		assertEquals(1, updates);
		verify(core).setRuntimeEnvironment(any(RuntimeEnvironment.class));
	}

	@Test
	public void shouldUpdateCoreOnAutoBuild() throws CoreException {
		InfinitestCore core = prepateCore(projectAUri(), 10);

		workspace.updateProjects();

		assertStatusIs(findingTests(0));
		verify(core).setRuntimeEnvironment(any(RuntimeEnvironment.class));

	}

	private InfinitestCore prepateCore(URI projectAUri, int numberOfTestsRun) {
		InfinitestCore core = mock(InfinitestCore.class);
		when(core.update()).thenReturn(numberOfTestsRun);
		when(coreRegistry.getCore(projectAUri)).thenReturn(core);
		return core;
	}

	@Test
	public void shouldSetAppropriateStatusIfNoTestsWereRun() throws CoreException {
		InfinitestCore core = prepateCore(projectAUri(), 0);

		workspace.updateProjects();

		assertStatusIs(noTestsRun());
		verify(core).setRuntimeEnvironment(any(RuntimeEnvironment.class));
	}

	@Test
	public void shouldSetWarningStatusIfNoTestsAreRun() throws CoreException {
		projects.clear();

		workspace.updateProjects();

		assertStatusIs(noTestsRun());
	}

	@Test
	public void shouldFireEventIfStatusChanges() throws CoreException {
		projects.clear();
		workspace.addStatusListeners(new WorkspaceStatusListener() {
			@Override
			public void statusChanged(WorkspaceStatus newStatus) {
				updatedStatus = newStatus;
			}
		});

		workspace.updateProjects();
		assertThat(updatedStatus, equalsStatus(noTestsRun()));
	}

	@Test
	public void shouldUpdateAllCoresWhenOneChanges() throws CoreException {
		String projectBName = "/projectB";
		JavaProjectBuilder projectB = project(projectBName);
		URI projectBUri = projectB.getProject().getLocationURI();
		projects.add(newFacade(projectB));

		InfinitestCore coreA = prepateCore(projectAUri(), 10);
		InfinitestCore coreB = prepateCore(projectBUri, 10);

		workspace.updateProjects();
		verify(coreA).setRuntimeEnvironment(any(RuntimeEnvironment.class));
		verify(coreB).setRuntimeEnvironment(any(RuntimeEnvironment.class));
	}

	private void assertStatusIs(WorkspaceStatus expectedStatus) {
		assertThat(workspace.getStatus(), equalsStatus(expectedStatus));
	}
}
