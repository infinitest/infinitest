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

import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.*;

import org.eclipse.core.internal.events.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.*;

public class WhenRespondingToCleanEvents extends ResourceEventSupport {
	private CleanEventProcessor processor;
	private CoreRegistry coreRegistry;

	@Before
	public void inContext() {
		coreRegistry = mock(CoreRegistry.class);
		processor = new CleanEventProcessor(coreRegistry, mock(ProjectSet.class));
	}

	@Test
	public void shouldRemoveCoreOnACleanBuild() throws JavaModelException {
		processor = new CleanEventProcessor(coreRegistry, new FakeProjectSet(project));
		processEvent(cleanBuildEvent());

		URI projectAUri = projectAUri();
		verify(coreRegistry).removeCore(projectAUri);
	}

	@Test
	public void shouldHandleACleanBuildOnAnUnIndexedProject() throws JavaModelException {
		processEvent(cleanBuildEvent());
		verifyZeroInteractions(coreRegistry);
	}

	@Test
	public void shouldOnlyRespondToPostBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_CHANGE, CLEAN_BUILD, null);
		assertFalse(processor.canProcessEvent(event));
	}

	private void processEvent(ResourceChangeEvent event) throws JavaModelException {
		assertTrue(processor.canProcessEvent(event));
		processor.processEvent(event);
	}
}
