package org.infinitest.eclipse.markers;

public interface MarkerRegistry
{
    void addMarker(MarkerInfo marker);

    void removeMarker(MarkerInfo marker);

    void clear();

    void updateMarker(MarkerInfo marker);

    void removeMarkers(String testName);
}
