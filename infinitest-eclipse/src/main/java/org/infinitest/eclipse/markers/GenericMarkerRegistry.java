/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.eclipse.markers;

import static org.infinitest.util.InfinitestUtils.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.ide.undo.UpdateMarkersOperation;
import org.infinitest.eclipse.InfinitestPlugin;

/**
 * This would be a nice feature someday: <a
 * href="http://help.eclipse.org/ganymede/topic/org.eclipse.platform.doc.isv
 * /reference/extension-points/org_eclipse_ui_ide_markerImageProviders.html"
 * >MarkerImageProvider tutorial</a>
 */
public class GenericMarkerRegistry implements MarkerRegistry {
	private final Map<MarkerInfo, IMarker> markers;
	private final String markerId;
	private int markerSeverity;

	public GenericMarkerRegistry(String markerId, String severityPerferrenceKey) {
		this(markerId, getPreferredSeverity(severityPerferrenceKey));
	}

	public GenericMarkerRegistry(String markerId, int markerSeverity) {
		this.markerId = markerId;
		this.markerSeverity = markerSeverity;
		markers = new HashMap<>();
	}

	private static int getPreferredSeverity(String severityPerferrenceKey) {
		InfinitestPlugin plugin = InfinitestPlugin.getInstance();
		// The plugin is null in unit tests
		if (plugin != null) {
			String value = plugin.getPreferenceStore().getString(severityPerferrenceKey);
			if (value != null && !value.isEmpty()) {
				try {
					return Integer.parseInt(value);
				} catch (NumberFormatException e) {
					log("Error getting preferred marker severity for " + severityPerferrenceKey, e);
				}
			}
		}
		
		return IMarker.SEVERITY_ERROR;
	}

	@Override
	public void addMarker(MarkerInfo newMarkerInfo) {
		if (!markers.containsKey(newMarkerInfo)) {
			IMarker newMarker = addMarkerToSource(newMarkerInfo);
			markers.put(newMarkerInfo, newMarker);
		} else {
			updateMarker(newMarkerInfo);
		}
	}

	@Override
	public void updateMarker(MarkerInfo markerInfo) {
		removeMarker(markerInfo);
		addMarker(markerInfo);
	}

	@Override
	public void removeMarker(MarkerInfo markerInfo) {
		IMarker marker = markers.remove(markerInfo);
		try {
			if (marker != null) {
				marker.delete();
			}
		} catch (CoreException e) {
			log("Error removing markers for " + markerInfo, e);
		}
	}

	private IMarker addMarkerToSource(MarkerInfo newMarker) {
		return newMarker.createMarker(markerId);
	}

	Set<MarkerInfo> getMarkers() {
		return markers.keySet();
	}

	@Override
	public void clear() {
		for (MarkerInfo each : copyOf(markers.keySet())) {
			deleteMarker(each);
		}
		markers.clear();
	}

	private Set<MarkerInfo> copyOf(Set<MarkerInfo> keySet) {
		// Make a copy to prevent concurrent modification while deleting markers
		return new HashSet<>(keySet);
	}

	@Override
	public void removeMarkers(String testName) {
		for (MarkerInfo each : findMarkersFor(testName)) {
			deleteMarker(each);
		}
	}
	
	@Override
	public void setMarkers(String testName, Collection<MarkerInfo> markers) {
		Collection<MarkerInfo> markersToProcess = new HashSet<>(markers);
		
		// Update the current markers
		for (MarkerInfo marker : findMarkersFor(testName)) {
			if (markersToProcess.contains(marker)) {
				updateMarker(marker);
				markersToProcess.remove(marker);
			} else {
				removeMarker(marker);
			}
		}
		// Add the new markers
		for (MarkerInfo marker : markersToProcess) {
			addMarker(marker);
		}
	}

	private void deleteMarker(MarkerInfo each) {
		try {
			// RISK Delete call is untested
			markers.remove(each).delete();
		} catch (CoreException e) {
			log("Error removing marker " + each, e);
		}
	}

	private Iterable<MarkerInfo> findMarkersFor(String testName) {
		List<MarkerInfo> markersFound = new ArrayList<>();
		for (MarkerInfo each : markers.keySet()) {
			if (each.getTestName().equals(testName)) {
				markersFound.add(each);
			}
		}
		return markersFound;
	}

	public int markerCount() {
		return getMarkers().size();
	}
	
	@Override
	public void updateMarkersSeverity(int markersSeverity) {
		this.markerSeverity = markersSeverity;
		
		Map<String, Integer> attributes = Collections.singletonMap(IMarker.SEVERITY, markerSeverity);
		IMarker[] markersArray = markers.values().toArray(new IMarker[0]);
		
		IProgressMonitor monitor = new NullProgressMonitor();
		
		for (IMarker marker : markers.values()) {
			UpdateMarkersOperation operation = buildUpdateMarkersAction(attributes, markersArray);
			
			try {
				operation.execute(monitor, marker.getResource());
			} catch (ExecutionException e) {
				log("Error updating marker severity " + marker, e);
			}
		}
	}

	protected UpdateMarkersOperation buildUpdateMarkersAction(Map<String, Integer> attributes, IMarker[] markers) {
		return new UpdateMarkersOperation(markers, attributes, "Infinitest markers update", true);
	}
	
	@Override
	public int markerServerity() {
		return markerSeverity;
	}
}
