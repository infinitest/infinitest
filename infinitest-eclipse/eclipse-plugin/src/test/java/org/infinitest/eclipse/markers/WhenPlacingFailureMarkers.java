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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.infinitest.eclipse.workspace.ResourceLookup;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingFailureMarkers
{
    private ResourceLookup lookup;
    private MarkerPlacer markerPlacer;
    private List<MarkerPlacementStrategy> links;

    @Before
    public void inContext()
    {
        lookup = mock(ResourceLookup.class);
        markerPlacer = new MarkerPlacer(lookup);
        links = markerPlacer.getLinks();
    }

    @Test
    public void shouldFirstTryToPlaceMarkersAtThePointOfFailure()
    {
        assertThat(links.get(0), instanceOf(PointOfFailurePlacementStrategy.class));
    }

    @Test
    public void shouldThenTryToPlaceMarkersUsingStackTraceInfo()
    {
        assertThat(links.get(1), instanceOf(StackTracePlacementStrategy.class));
    }

    @Test
    public void shouldThenTryToPlaceMarkersInTheOriginatingTest()
    {
        assertThat(links.get(2), instanceOf(TestNamePlacementStrategy.class));
    }

    @Test
    public void shouldDefaultToWorkspaceRootIfNoStrategiesMatch()
    {
        assertThat(links.get(3), instanceOf(WorkspaceFallbackPlacementStrategy.class));
    }
}
