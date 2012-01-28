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

import org.eclipse.core.internal.events.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.*;

public class WhenRespondingToBuildEvents extends ResourceEventSupport {
	private ClassFileChangeProcessor processor;
	private WorkspaceFacade workspace;

	@Before
	public void inContext() {
		workspace = mock(WorkspaceFacade.class);
		processor = new ClassFileChangeProcessor(workspace);
	}

	@After
	public void verifyWorkspace() {
		verifyZeroInteractions(workspace);
	}

	@Test
	public void shouldNotRespondToPreBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_BUILD, AUTO_BUILD, null);
		assertFalse(processor.canProcessEvent(event));
	}

	@Test
	public void shouldNotUpdateIfClassesAreNotChanged() throws CoreException {
		processor.processEvent(emptyEvent());
	}

	@Test
	public void shouldRespondToPostBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, null);
		assertTrue(processor.canProcessEvent(event));
	}

	@Test
	public void shouldRespondToPostChangeEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_CHANGE, AUTO_BUILD, null);
		assertTrue(processor.canProcessEvent(event));
	}
}
