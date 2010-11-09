package org.infinitest.eclipse.markers;

import static org.mockito.Mockito.*;

import org.junit.Test;

public class WhenProjectsAreUpdated
{
    @Test
    public void canClearMarkersInTheMarkerRegistry()
    {
        SlowMarkerRegistry markerRegistry = mock(SlowMarkerRegistry.class);
        MarkerClearingObserver observer = new MarkerClearingObserver(markerRegistry);

        observer.projectsUpdated();

        verify(markerRegistry).clear();
    }
}
