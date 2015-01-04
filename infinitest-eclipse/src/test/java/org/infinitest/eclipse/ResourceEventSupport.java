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
		return createResourceDelta(project);
	}

	protected IResourceDelta createDelta(JavaProjectBuilder project) {
		IResourceDelta classResource = mock(IResourceDelta.class);
		when(classResource.getFullPath()).thenReturn(new Path("a.class"));

		return createResourceDelta(project, classResource);
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
