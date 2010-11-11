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

import static com.google.common.collect.Lists.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersInOriginatingTest
{
    private TestNamePlacementStrategy strategy;
    private TestEvent event;
    private IResource testResource;
    private ResourceLookup lookup;

    @Before
    public void inContext()
    {
        testResource = mock(IResource.class);
        lookup = mock(ResourceLookup.class);
        strategy = new TestNamePlacementStrategy(lookup);
        event = eventWithError(new AssertionError());
    }

    @Test
    public void shouldUseTestNameInEvent()
    {
        when(lookup.findResourcesForClassName(TEST_NAME)).thenReturn(newArrayList(testResource));

        MarkerPlacement placement = strategy.getPlacement(event);

        assertSame(testResource, placement.getResource());
        assertEquals("Line zero indicates we don't know where to place the marker", 0, placement.getLineNumber());
    }

    @Test
    public void shouldReturnNullIfTestCannotBeFoundInWorkspace()
    {
        assertNull(strategy.getPlacement(event));
    }
}
