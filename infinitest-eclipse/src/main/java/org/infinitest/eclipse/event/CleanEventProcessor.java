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
package org.infinitest.eclipse.event;

import static java.util.logging.Level.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.infinitest.eclipse.workspace.CoreRegistry;
import org.infinitest.eclipse.workspace.EclipseProject;
import org.infinitest.eclipse.workspace.ProjectSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class CleanEventProcessor extends EclipseEventProcessor
{
    private final CoreRegistry coreRegistry;
    private final ProjectSet projectSet;

    @Autowired
    CleanEventProcessor(CoreRegistry coreRegistry, ProjectSet projectSet)
    {
        super("Clearing Infinitest Indexes");
        this.coreRegistry = coreRegistry;
        this.projectSet = projectSet;
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws JavaModelException
    {
        cleanProjects(getDeltas(event));
    }

    private void cleanProjects(IResourceDelta[] projectResourceDeltas)
    {
        for (IResourceDelta projectDelta : projectResourceDeltas)
        {
            cleanProject(projectDelta);
        }
    }

    private void cleanProject(IResourceDelta projectResourceDelta)
    {
        IPath projectPath = projectResourceDelta.getResource().getFullPath();
        EclipseProject project = projectSet.findProject(projectPath);
        if (project == null)
        {
            log(WARNING, "Could not find project for resource " + projectPath);
        }
        else
        {
            coreRegistry.removeCore(project.getLocationURI());
        }
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        return event.getBuildKind() == CLEAN_BUILD && (event.getType() & POST_BUILD) > 0;
    }

}
