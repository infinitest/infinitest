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
package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Iterables.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.markers.ResourceLookupAdapter;
import org.junit.Before;
import org.junit.Test;

public class WhenLookingForResources
{
    private ResourceLookupAdapter adapter;
    private IResource mockResource;
    private ResourceFinder finder;

    @Before
    public void inContext()
    {
        mockResource = mock(IResource.class);
        finder = mock(ResourceFinder.class);
        adapter = new ResourceLookupAdapter(finder);
    }

    @Test
    public void shouldFindAllMatchingResourcesBasedOnClassName()
    {
        when(finder.findResourceForSourceFile("com/foo/Bar.java")).thenReturn(mockResource);

        List<IResource> resources = adapter.findResourcesForClassName("com.foo.Bar");

        assertEquals(mockResource, getOnlyElement(resources));
    }

    @Test
    public void shouldReturnEmptyListIfNoResourcesMatch()
    {
        assertTrue(adapter.findResourcesForClassName("com.foo.Bar").isEmpty());
    }
}
