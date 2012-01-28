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

import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.mockito.Mockito.*;

import java.net.*;

import org.eclipse.core.internal.events.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.workspace.*;

public class ResourceEventSupport {
	protected static final String PROJECT_A_NAME = "/projectA";
	protected JavaProjectBuilder project = project(PROJECT_A_NAME);

	protected ResourceChangeEvent autoBuildEvent() {
		return buildEventWith(createDelta());
	}

	protected ResourceChangeEvent buildEventWith(IResourceDelta createDelta) {
		return new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, createDelta);
	}

	protected ResourceChangeEvent cleanBuildEvent() {
		return new ResourceChangeEvent(this, POST_BUILD, CLEAN_BUILD, createDelta());
	}

	protected ResourceChangeEvent emptyEvent() {
		return new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, createEmptyDelta());
	}

	protected IResourceDelta createDelta() {
		return createDelta(project);
	}

	protected URI projectAUri() {
		return project.getProject().getLocationURI();
	}

	protected IResourceDelta createEmptyDelta() {
		return createResourceDelta(project, new IResourceDelta[] {});
	}

	protected IResourceDelta createDelta(JavaProjectBuilder project) {
		IResourceDelta classResource = mock(IResourceDelta.class);
		when(classResource.getFullPath()).thenReturn(new Path("a.class"));

		return createResourceDelta(project, new IResourceDelta[] { classResource });
	}

	protected IResourceDelta createResourceDelta(JavaProjectBuilder project, IResourceDelta... resourceDeltas) {
		IResourceDelta projectDelta = mock(IResourceDelta.class);
		IResource resource = project.getResource();
		when(projectDelta.getResource()).thenReturn(resource);
		when(projectDelta.getFullPath()).thenReturn(new Path("/workspace/projectA"));
		when(projectDelta.getAffectedChildren()).thenReturn(resourceDeltas);

		IResourceDelta delta = mock(IResourceDelta.class);
		when(delta.getAffectedChildren()).thenReturn(new IResourceDelta[] { projectDelta });
		return delta;
	}
}
