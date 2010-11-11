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
package org.infinitest.eclipse.markers;

import static java.util.Arrays.*;
import static org.infinitest.eclipse.workspace.FakeResourceFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersInJavaClasses
{
    private TestEvent event;
    private ResourceLookup lookup;
    private PointOfFailure pof;
    private IResource resource;
    private MarkerPlacementStrategy placementStrategy;

    @Before
    public void inContext()
    {
        event = mock(TestEvent.class);
        lookup = mock(ResourceLookup.class);
        pof = new PointOfFailure("com.myproject.MyClass", 70, "NullPointerException", "");
        resource = stubResource("/projectA/com/myproject/MyClass.java");
        placementStrategy = new PointOfFailurePlacementStrategy(lookup);
    }

    @Test
    public void shouldTryToUseClassNameFromPointOfFailure()
    {
        when(lookup.findResourcesForClassName("com.myproject.MyClass")).thenReturn(asList(resource));
        when(event.getPointOfFailure()).thenReturn(pof);

        MarkerPlacement placement = placementStrategy.getPlacement(event);

        assertSame(resource, placement.getResource());
        assertEquals(70, placement.getLineNumber());
    }

    @Test
    public void shouldReturnNullIfResourceCannotBeFound()
    {
        when(event.getPointOfFailure()).thenReturn(pof);
        when(lookup.findResourcesForClassName(anyString())).thenReturn(Collections.<IResource> emptyList());

        MarkerPlacement placement = placementStrategy.getPlacement(event);

        assertNull(placement);
    }
}
