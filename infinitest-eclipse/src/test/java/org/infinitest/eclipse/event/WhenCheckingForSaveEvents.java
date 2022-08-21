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

import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceDelta.CONTENT;
import static org.eclipse.core.resources.IResourceDelta.MARKERS;
import static org.eclipse.core.resources.IncrementalProjectBuilder.AUTO_BUILD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.trim.SaveListener;
import org.junit.Before;
import org.junit.Test;

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
		javaResource.accept(any(IResourceDeltaVisitor.class));

		return createResourceDelta(project, new IResourceDelta[] { javaResource });
	}
}
