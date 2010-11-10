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
