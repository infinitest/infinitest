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
import static org.infinitest.eclipse.InfinitestCoreClasspath.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;
import static org.infinitest.util.Events.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.infinitest.InfinitestCore;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.eclipse.InfinitestPlugin;
import org.infinitest.eclipse.UpdateListener;
import org.infinitest.eclipse.status.WorkspaceStatus;
import org.infinitest.eclipse.status.WorkspaceStatusListener;
import org.infinitest.util.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class EclipseWorkspace implements WorkspaceFacade
{
    private final CoreRegistry coreRegistry;
    private final CoreFactory coreFactory;
    private WorkspaceStatus status;
    private final List<WorkspaceStatusListener> statusListeners = newArrayList();
    private final Events<UpdateListener> updateEvent = eventFor(UpdateListener.class);
    private final ProjectSet projectSet;

    @Autowired
    EclipseWorkspace(ProjectSet projectSet, CoreRegistry coreRegistry, CoreFactory coreFactory)
    {
        this.projectSet = projectSet;
        this.coreRegistry = coreRegistry;
        this.coreFactory = coreFactory;
    }

    @Autowired
    public void addStatusListeners(WorkspaceStatusListener... listeners)
    {
        for (WorkspaceStatusListener each : listeners)
        {
            statusListeners.add(each);
        }
    }

    public void updateProjects() throws CoreException
    {
        if (projectSet.hasErrors())
        {
            setStatus(workspaceErrors());
        }
        else
        {
            int numberOfTestsToRun = updateProjectsIn(projectSet);
            if (numberOfTestsToRun == 0)
            {
                setStatus(noTestsRun());
            }
        }
    }

    public void setStatus(WorkspaceStatus newStatus)
    {
        status = newStatus;
        for (WorkspaceStatusListener each : statusListeners)
        {
            each.statusChanged(newStatus);
        }
    }

    public WorkspaceStatus getStatus()
    {
        return status;
    }

    private int updateProjectsIn(ProjectSet projectSet) throws CoreException
    {
        updateEvent.fire();
        int totalTests = 0;
        for (ProjectFacade project : projectSet.projects())
        {
            setStatus(findingTests(totalTests));
            totalTests += updateProject(projectSet, project);
        }
        return totalTests;
    }

    private int updateProject(ProjectSet projectSet, ProjectFacade project) throws CoreException
    {
        RuntimeEnvironment environment = buildRuntimeEnvironment(project);
        InfinitestCore core = coreRegistry.getCore(project.getLocationURI());
        if (core == null)
        {
            core = createCore(project, environment);
        }
        core.setRuntimeEnvironment(environment);
        return core.update();
    }

    public RuntimeEnvironment buildRuntimeEnvironment(ProjectFacade project) throws CoreException
    {
        File javaHome = project.getJvmHome();
        RuntimeEnvironment environment = buildRuntimeEnvironment(project, javaHome);
        environment.setInfinitestRuntimeClassPath(getCoreJarLocation(InfinitestPlugin.getInstance()).getAbsolutePath());
        return environment;
    }

    private RuntimeEnvironment buildRuntimeEnvironment(ProjectFacade project, File javaHome) throws CoreException
    {
        return new RuntimeEnvironment(projectSet.outputDirectories(project), project.workingDirectory(),
                        project.rawClasspath(), javaHome);
    }

    private InfinitestCore createCore(ProjectFacade project, RuntimeEnvironment environment)
    {
        InfinitestCore core = coreFactory.createCore(project.getLocationURI(), project.getName(), environment);
        coreRegistry.addCore(project.getLocationURI(), core);
        log("Added core " + core.getName() + " with classpath " + environment.getCompleteClasspath());
        return core;
    }

    public void addUpdateListeners(UpdateListener... updateListeners)
    {
        for (UpdateListener each : updateListeners)
        {
            updateEvent.addListener(each);
        }
    }
}
