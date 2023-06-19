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

import static org.infinitest.util.InfinitestGlobalSettings.*;

import java.util.*;

import org.infinitest.*;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class SlowTestObserver implements DisabledTestListener, TestResultsListener {
	private final MarkerRegistry slowMarkerRegistry;
	private final ResourceLookup lookup;

	@Autowired
	public SlowTestObserver(SlowMarkerRegistry slowMarkerRegistry, ResourceLookup lookup) {
		this.slowMarkerRegistry = slowMarkerRegistry;
		this.lookup = lookup;
	}

	@Override
	public void testCaseStarting(TestEvent event) {
	}

	@Override
	public void testCaseComplete(TestCaseEvent event) {
		Collection<MarkerInfo> markers = new ArrayList<>();
		
		for (MethodStats methodStat : event.getRunStats()) {
			if (methodStat.duration() > getSlowTestTimeLimit()) {
				SlowTestMarkerInfo marker = new SlowTestMarkerInfo(event.getTestName(), methodStat, lookup, slowMarkerRegistry.markerServerity());
				markers.add(marker);
			}
		}
		
		slowMarkerRegistry.setMarkers(event.getTestName(), markers);
	}

	@Override
	public void testsDisabled(Collection<String> testName) {
		for (String eachTest : testName) {
			slowMarkerRegistry.removeMarkers(eachTest);
		}
	}

	public void clearMarkers() {
		slowMarkerRegistry.clear();
	}
}
