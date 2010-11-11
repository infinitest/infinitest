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
import static com.google.common.collect.Sets.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.google.common.collect.Maps;

/**
 * This would be a nice feature someday: <a
 * href="http://help.eclipse.org/ganymede/topic/org.eclipse.platform.doc.isv
 * /reference/extension-points/org_eclipse_ui_ide_markerImageProviders.html" >MarkerImageProvider
 * tutorial</a>
 */
public class GenericMarkerRegistry implements MarkerRegistry
{
    private final Map<MarkerInfo, IMarker> markers;
    private final String markerId;

    public GenericMarkerRegistry(String markerId)
    {
        this.markerId = markerId;
        markers = Maps.newHashMap();
    }

    public void addMarker(MarkerInfo newMarkerInfo)
    {
        if (!markers.containsKey(newMarkerInfo))
        {
            IMarker newMarker = addMarkerToSource(newMarkerInfo);
            markers.put(newMarkerInfo, newMarker);
        }
        else
        {
            updateMarker(newMarkerInfo);
        }
    }

    public void updateMarker(MarkerInfo markerInfo)
    {
        removeMarker(markerInfo);
        addMarker(markerInfo);
    }

    public void removeMarker(MarkerInfo markerInfo)
    {
        IMarker marker = markers.remove(markerInfo);
        try
        {
            if (marker != null)
            {
                marker.delete();
            }
        }
        catch (CoreException e)
        {
            log("Error removing markers for " + markerInfo, e);
        }
    }

    private IMarker addMarkerToSource(MarkerInfo newMarker)
    {
        return newMarker.createMarker(markerId);
    }

    Set<MarkerInfo> getMarkers()
    {
        return markers.keySet();
    }

    public void clear()
    {
        for (MarkerInfo each : copyOf(markers.keySet()))
        {
            deleteMarker(each);
        }
        markers.clear();
    }

    private Set<MarkerInfo> copyOf(Set<MarkerInfo> keySet)
    {
        // Make a copy to prevent concurrent modification while deleting markers
        return newHashSet(keySet);
    }

    public void removeMarkers(String testName)
    {
        for (MarkerInfo each : findMarkersFor(testName))
        {
            deleteMarker(each);
        }
    }

    private void deleteMarker(MarkerInfo each)
    {
        try
        {
            // RISK Delete call is untested
            markers.remove(each).delete();
        }
        catch (CoreException e)
        {
            log("Error removing marker " + each, e);
        }
    }

    private Iterable<MarkerInfo> findMarkersFor(String testName)
    {
        List<MarkerInfo> markersFound = newArrayList();
        for (MarkerInfo each : markers.keySet())
        {
            if (each.getTestName().equals(testName))
            {
                markersFound.add(each);
            }
        }
        return markersFound;
    }

    public int markerCount()
    {
        return getMarkers().size();
    }
}
