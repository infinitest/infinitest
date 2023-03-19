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
package org.infinitest.eclipse;

import static java.util.Arrays.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.resources.IMarker;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenWatchingForSlowTests {
	private SlowMarkerRegistry mockMarkerRegistry;
	private MethodStats methodStats;
	private MarkerInfo expectedMarker;
	private SlowTestObserver observer;
	private TestCaseEvent event;

	@BeforeEach
	void inContext() {
		mockMarkerRegistry = mock(SlowMarkerRegistry.class);
		methodStats = new MethodStats("shouldRunSlowly");
		ResourceLookup resourceLookup = mock(ResourceLookup.class);
		expectedMarker = new SlowTestMarkerInfo("MyTest", methodStats, resourceLookup, IMarker.SEVERITY_ERROR);
		setSlowTestTimeLimit(2000);
		observer = new SlowTestObserver(mockMarkerRegistry, resourceLookup);

		TestResults results = new TestResults();
		results.addMethodStats(asList(methodStats));
		event = new TestCaseEvent("MyTest", this, results);
	}

	@AfterEach
	void cleanup() {
		resetToDefaults();
	}

	@Test
	void shouldAddMarkersForSlowTests() {
		methodStats.start(1000);
		methodStats.stop(5000);

		observer.testCaseComplete(event);

		verify(mockMarkerRegistry).addMarker(expectedMarker);
	}

	@Test
	void shouldRemoveMarkersForFastTests() {
		methodStats.start(1000);
		methodStats.stop(1001);

		observer.testCaseComplete(event);

		verify(mockMarkerRegistry).removeMarker(expectedMarker);
	}

	@Test
	void shouldRemoveMarkersWhenTestsAreDisabled() {
		methodStats.start(1000);
		methodStats.stop(5000);

		observer.testCaseComplete(event);
		observer.testsDisabled(Arrays.asList("MyTest"));

		verify(mockMarkerRegistry).addMarker(expectedMarker);
		verify(mockMarkerRegistry).removeMarkers("MyTest");
	}

	@Test
	void canRemoveAllMarkers() {
		observer.clearMarkers();

		verify(mockMarkerRegistry).clear();
	}
}
