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
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersUsingAStackTrace
{
    private static final int SECOND_ELEMENT_LINE_NUMBER = 25;
    private StackTracePlacementStrategy strategy;
    private Throwable error;
    private IResource mockResource;

    @Before
    public void inContext()
    {
        ResourceLookup lookup = mock(ResourceLookup.class);
        mockResource = mock(IResource.class);
        when(lookup.findResourcesForClassName("com.myco.Foo")).thenReturn(asList(mockResource));
        strategy = new StackTracePlacementStrategy(lookup);
        error = mock(Throwable.class);
    }

    @Test
    public void shouldFindTheFirstClassInTheWorkspace()
    {
        expectStackTraceWithClass("com.myco.Foo");

        MarkerPlacement placement = strategy.getPlacement(eventWithError(error));

        assertEquals(SECOND_ELEMENT_LINE_NUMBER, placement.getLineNumber());
        assertSame(mockResource, placement.getResource());
    }

    @Test
    public void shouldReturnNullIfNoClassesAreInTheWorkspace()
    {
        expectStackTraceWithClass("com.myco.Baz");

        assertNull(strategy.getPlacement(eventWithError(error)));
    }

    private void expectStackTraceWithClass(String secondElementClass)
    {
        StackTraceElement[] stackTrace = new StackTraceElement[] {
                        new StackTraceElement("com.myco.Bar", "method", "file", 10),
                        new StackTraceElement(secondElementClass, "method", "file", SECOND_ELEMENT_LINE_NUMBER) };
        when(error.getStackTrace()).thenReturn(stackTrace);
    }
}
