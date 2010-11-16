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
package org.infinitest.eclipse;

import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;

import java.net.URI;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.Path;
import org.infinitest.eclipse.workspace.JavaProjectBuilder;

public class ResourceEventSupport
{
    protected static final String PROJECT_A_NAME = "/projectA";
    protected JavaProjectBuilder project = project(PROJECT_A_NAME);

    protected ResourceChangeEvent autoBuildEvent()
    {
        return buildEventWith(createDelta());
    }

    protected ResourceChangeEvent buildEventWith(IResourceDelta createDelta)
    {
        return new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, createDelta);
    }

    protected ResourceChangeEvent cleanBuildEvent()
    {
        return new ResourceChangeEvent(this, POST_BUILD, CLEAN_BUILD, createDelta());
    }

    protected ResourceChangeEvent emptyEvent()
    {
        return new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, createEmptyDelta());
    }

    protected IResourceDelta createDelta()
    {
        return createDelta(project);
    }

    protected URI projectAUri()
    {
        return project.getProject().getLocationURI();
    }

    protected IResourceDelta createEmptyDelta()
    {
        return createResourceDelta(project, new IResourceDelta[] {});
    }

    protected IResourceDelta createDelta(JavaProjectBuilder project)
    {
        IResourceDelta classResource = createMock(IResourceDelta.class);
        expect(classResource.getFullPath()).andReturn(new Path("a.class"));

        return createResourceDelta(project, new IResourceDelta[] { classResource });
    }

    protected IResourceDelta createResourceDelta(JavaProjectBuilder project, IResourceDelta... resourceDeltas)
    {
        IResourceDelta projectDelta = createMock(IResourceDelta.class);
        expect(projectDelta.getResource()).andReturn(project.getResource());
        expect(projectDelta.getFullPath()).andReturn(new Path("/workspace/projectA"));
        expect(projectDelta.getAffectedChildren()).andReturn(resourceDeltas);

        IResourceDelta delta = createMock(IResourceDelta.class);
        expect(delta.getAffectedChildren()).andReturn(new IResourceDelta[] { projectDelta });
        replay(delta, projectDelta);
        for (IResourceDelta each : resourceDeltas)
        {
            replay(each);
        }
        return delta;
    }
}
