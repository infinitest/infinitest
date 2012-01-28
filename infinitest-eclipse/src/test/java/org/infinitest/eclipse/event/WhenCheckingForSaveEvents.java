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
import static org.eclipse.core.resources.IResourceDelta.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.internal.events.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.trim.*;
import org.junit.*;

public class WhenCheckingForSaveEvents extends ResourceEventSupport {
	private SaveDetector detector;
	private DeltaVisitor deltaVisitor;
	private IResource resource;

	@Before
	public void inContext() {
		detector = new SaveDetector(mock(SaveListener.class));
		deltaVisitor = new DeltaVisitor();
		resource = mock(IResource.class);
	}

	@Test
	public void shouldLookForSavedResourcesUsingVisitor() throws CoreException {
		IResourceDelta delta = mock(IResourceDelta.class);

		assertFalse(detector.canProcessEvent(buildEventWith(delta)));

		verify(delta).accept(any(DeltaVisitor.class), eq(true));
	}

	@Test
	public void shouldNotifyListenerWhenSavedResourceIsFound() throws CoreException {
		SaveListener listener = mock(SaveListener.class);

		detector = new SaveDetector(listener);
		detector.processEvent(null);

		verify(listener).filesSaved();
	}

	@Test
	public void shouldIgnoreDerivedResourcesEvents() throws CoreException {
		when(resource.isDerived()).thenReturn(true);

		assertFalse(deltaVisitor.visit(resourceDelta(CONTENT)));
		assertFalse(deltaVisitor.savedResourceFound());
	}

	@Test
	public void shouldDetectNonDerivedResources() throws CoreException {
		when(resource.getType()).thenReturn(IResource.FILE);
		when(resource.isDerived()).thenReturn(false);

		assertFalse(deltaVisitor.visit(resourceDelta(CONTENT)));
		assertTrue(deltaVisitor.savedResourceFound());
	}

	@Test
	public void shouldKeepLookingIfResourceIsNotAFile() throws CoreException {
		when(resource.isDerived()).thenReturn(false);
		when(resource.getType()).thenReturn(IResource.FOLDER);

		assertTrue(deltaVisitor.visit(resourceDelta(CONTENT)));
		assertFalse(deltaVisitor.savedResourceFound());
	}

	@Test
	public void shouldIgnoreMarkerOnlyChanges() throws CoreException {
		when(resource.getType()).thenReturn(IResource.FILE);
		when(resource.isDerived()).thenReturn(false);

		assertFalse(deltaVisitor.visit(resourceDelta(MARKERS)));
		assertFalse(deltaVisitor.savedResourceFound());
	}

	private IResourceDelta resourceDelta(int flags) {
		IResourceDelta classResourceDelta = mock(IResourceDelta.class);
		when(classResourceDelta.getResource()).thenReturn(resource);
		when(classResourceDelta.getFlags()).thenReturn(flags);
		return classResourceDelta;
	}

	protected ResourceChangeEvent saveEvent() throws CoreException {
		return new ResourceChangeEvent(this, POST_CHANGE, AUTO_BUILD, createSaveDelta());
	}

	protected IResourceDelta createSaveDelta() throws CoreException {
		IResourceDelta javaResource = mock(IResourceDelta.class);
		when(javaResource.getFullPath()).thenReturn(new Path("a.java"));
		javaResource.accept((IResourceDeltaVisitor) anyObject());

		return createResourceDelta(project, new IResourceDelta[] { javaResource });
	}
}
