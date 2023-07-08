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
package org.infinitest.eclipse.event;

import static org.eclipse.core.resources.IResourceChangeEvent.PRE_BUILD;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_CLOSE;
import static org.eclipse.core.resources.IncrementalProjectBuilder.AUTO_BUILD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.workspace.CoreRegistry;
import org.infinitest.eclipse.workspace.EclipseProject;
import org.infinitest.eclipse.workspace.ProjectSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CloseProjectEventProcessorTest extends ResourceEventSupport {
	private CoreRegistry coreRegistry;
	private ProjectSet projectSet;
	private CloseProjectEventProcessor processor;

	@BeforeEach
	void inContext() {
		coreRegistry = mock(CoreRegistry.class);
		projectSet = mock(ProjectSet.class);
		
		processor = new CloseProjectEventProcessor(coreRegistry, projectSet);
	}

	@Test
	void shouldNotRespondToPreBuildEvents() {
		// Not sure if it is correct to use AUTO_BUILD here
		
		IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_BUILD, AUTO_BUILD, null);
		assertFalse(processor.canProcessEvent(event));
	}

	@Test
	void shouldRespondToPreCloseEvents() {
		IResourceDelta delta = mock(IResourceDelta.class);
		IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_CLOSE, AUTO_BUILD, delta);
		assertTrue(processor.canProcessEvent(event));
	}
	
	@Test
	void shouldCloseCore() throws CoreException, URISyntaxException {
		IResourceChangeEvent event = mock(IResourceChangeEvent.class);
		IProject project = mock(IProject.class);
		IPath projectPath = mock(IPath.class);
		EclipseProject projectFacade = mock(EclipseProject.class);
		URI projectUri = new URI("c:/test/project");
		
		when(event.getResource()).thenReturn(project);
		when(project.getFullPath()).thenReturn(projectPath);
		when(projectSet.findProject(projectPath)).thenReturn(projectFacade);
		when(projectFacade.getLocationURI()).thenReturn(projectUri);
		
		processor.processEvent(event);
		
		verify(coreRegistry).removeCore(projectUri);
	}
}
