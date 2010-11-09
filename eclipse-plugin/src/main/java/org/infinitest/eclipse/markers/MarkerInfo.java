package org.infinitest.eclipse.markers;

import java.util.Map;

import org.eclipse.core.resources.IMarker;

public interface MarkerInfo
{
    IMarker createMarker(String markerId);

    // DEBT this doesn't need to be here. Should be hidden behind createMarker
    Map<String, Object> attributes();

    String getTestName();
}