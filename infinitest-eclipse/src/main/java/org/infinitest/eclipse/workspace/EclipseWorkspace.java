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

import static com.google.common.collect.Lists.newArrayList;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.findingTests;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.noTestsRun;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.workspaceErrors;
import static org.infinitest.util.Events.eventFor;
import static org.infinitest.util.InfinitestUtils.log;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.infinitest.InfinitestCore;
import org.infinitest.eclipse.InfinitestJarsLocator;
import org.infinitest.eclipse.UpdateListener;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.eclipse.status.WorkspaceStatusListener;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.util.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class EclipseWorkspace implements WorkspaceFacade {
	private final CoreRegistry coreRegistry;
	private final CoreFactory coreFactory;
	private WorkspaceStatus status;
	private final List<WorkspaceStatusListener> statusListeners = newArrayList();
	private final Events<UpdateListener> updateEvent = eventFor(UpdateListener.class);
	private final ProjectSet projectSet;
	private final InfinitestJarsLocator infinitestJarsClasspathProvider;

	@Autowired
	EclipseWorkspace(ProjectSet projectSet, 
			CoreRegistry coreRegistry, 
			CoreFactory coreFactory, 
			InfinitestJarsLocator infinitestJarsClasspathProvider) {
		this.projectSet = projectSet;
		this.coreRegistry = coreRegistry;
		this.coreFactory = coreFactory;
		this.infinitestJarsClasspathProvider = infinitestJarsClasspathProvider;
	}

	@Autowired
	public void addStatusListeners(WorkspaceStatusListener... listeners) {
		Collections.addAll(statusListeners, listeners);
	}

	@Override
	public void updateProjects() throws CoreException {
		if (projectSet.hasErrors()) {
			setStatus(workspaceErrors());
		} else {
			int numberOfTestsToRun = updateProjectsIn(projectSet);
			if (numberOfTestsToRun == 0) {
				setStatus(noTestsRun());
			}
		}
	}

	public void setStatus(WorkspaceStatus newStatus) {
		status = newStatus;
		for (WorkspaceStatusListener each : statusListeners) {
			each.statusChanged(newStatus);
		}
	}

	public WorkspaceStatus getStatus() {
		return status;
	}

	private int updateProjectsIn(ProjectSet projectSet) throws CoreException {
		updateEvent.fire();
		int totalTests = 0;
		
		List<ProjectFacade> projects = projectSet.projects();
		for (int i = 0; i < projects.size(); i++) {
			ProjectFacade project = projects.get(i);
			setStatus(findingTests(i, projects.size(), totalTests));
			totalTests += updateProject(project);
		}
		return totalTests;
	}

	private int updateProject(ProjectFacade project) throws CoreException {
		RuntimeEnvironment environment = buildRuntimeEnvironment(project);
		InfinitestCore core = coreRegistry.getCore(project.getLocationURI());
		if (core == null) {
			core = createCore(project, environment);
		}
		core.setRuntimeEnvironment(environment);
		return core.update();
	}

	public RuntimeEnvironment buildRuntimeEnvironment(ProjectFacade project) throws CoreException {
		File javaHome = project.getJvmHome();
		return buildRuntimeEnvironment(project, javaHome);
	}

	private RuntimeEnvironment buildRuntimeEnvironment(ProjectFacade project, File javaHome) throws CoreException {
		String runnerBootsrapClassPath = infinitestJarsClasspathProvider.getInfinitestClassLoaderClassPath();
		String runnerProcessClassPath = infinitestJarsClasspathProvider.getInfinitestRunnerClassPath();
		
		return new RuntimeEnvironment(javaHome, project.workingDirectory(), runnerBootsrapClassPath, runnerProcessClassPath, projectSet.outputDirectories(project), project.rawClasspath());
	}


	private InfinitestCore createCore(ProjectFacade project, RuntimeEnvironment environment) {
		InfinitestCore core = coreFactory.createCore(project.getName(), environment);
		coreRegistry.addCore(project.getLocationURI(), core);
		log("Added core " + core.getName() + " with classpath " + environment.getRunnerFullClassPath());
		return core;
	}

	public void addUpdateListeners(UpdateListener... updateListeners) {
		for (UpdateListener each : updateListeners) {
			updateEvent.addListener(each);
		}
	}
}
