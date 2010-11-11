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

import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IResourceDelta.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.junit.Assert.*;

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

public class WhenCheckingForSaveEvents extends ResourceEventSupport
{
    private SaveDetector detector;
    private DeltaVisitor deltaVisitor;
    private IResource resource;

    @Before
    public void inContext()
    {
        detector = new SaveDetector(createMock(SaveListener.class));
        deltaVisitor = new DeltaVisitor();
        resource = createMock(IResource.class);
    }

    @Test
    public void shouldLookForSavedResourcesUsingVisitor() throws CoreException
    {
        IResourceDelta delta = createMock(IResourceDelta.class);
        delta.accept(isA(DeltaVisitor.class), eq(true));
        replay(delta);

        assertFalse(detector.canProcessEvent(buildEventWith(delta)));
        verify(delta);
    }

    @Test
    public void shouldNotifyListenerWhenSavedResourceIsFound() throws CoreException
    {
        SaveListener listener = createMock(SaveListener.class);
        listener.filesSaved();
        replay(listener);
        detector = new SaveDetector(listener);
        detector.processEvent(null);
        verify(listener);
    }

    @Test
    public void shouldIgnoreDerivedResourcesEvents() throws CoreException
    {
        expect(resource.isDerived()).andReturn(true);
        replay(resource);

        assertFalse(deltaVisitor.visit(resourceDelta(CONTENT)));
        assertFalse(deltaVisitor.savedResourceFound());
        verify(resource);
    }

    @Test
    public void shouldDetectNonDerivedResources() throws CoreException
    {
        expect(resource.getType()).andReturn(IResource.FILE);
        expect(resource.isDerived()).andReturn(false);
        replay(resource);

        assertFalse(deltaVisitor.visit(resourceDelta(CONTENT)));
        assertTrue(deltaVisitor.savedResourceFound());
        verify(resource);
    }

    @Test
    public void shouldKeepLookingIfResourceIsNotAFile() throws CoreException
    {
        expect(resource.isDerived()).andReturn(false);
        expect(resource.getType()).andReturn(IResource.FOLDER);
        replay(resource);

        assertTrue(deltaVisitor.visit(resourceDelta(CONTENT)));
        assertFalse(deltaVisitor.savedResourceFound());
        verify(resource);
    }

    @Test
    public void shouldIgnoreMarkerOnlyChanges() throws CoreException
    {
        expect(resource.getType()).andReturn(IResource.FILE);
        expect(resource.isDerived()).andReturn(false);
        replay(resource);

        assertFalse(deltaVisitor.visit(resourceDelta(MARKERS)));
        assertFalse(deltaVisitor.savedResourceFound());
        verify(resource);
    }

    private IResourceDelta resourceDelta(int flags)
    {
        IResourceDelta classResourceDelta = createMock(IResourceDelta.class);
        expect(classResourceDelta.getResource()).andReturn(resource);
        expect(classResourceDelta.getFlags()).andReturn(flags);
        replay(classResourceDelta);
        return classResourceDelta;
    }

    protected ResourceChangeEvent saveEvent() throws CoreException
    {
        return new ResourceChangeEvent(this, POST_CHANGE, AUTO_BUILD, createSaveDelta());
    }

    protected IResourceDelta createSaveDelta() throws CoreException
    {
        IResourceDelta javaResource = createMock(IResourceDelta.class);
        expect(javaResource.getFullPath()).andReturn(new Path("a.java"));
        javaResource.accept((IResourceDeltaVisitor) anyObject());

        return createResourceDelta(project, new IResourceDelta[] { javaResource });
    }
}
