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

import static com.google.common.collect.Maps.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;

public class SlowTestMarkerInfo extends AbstractMarkerInfo {
	private final String testName;
	private final MethodStats methodStats;
	private final ResourceLookup lookup;
	private final int markerSeverity;
	
	public SlowTestMarkerInfo(String testName, MethodStats methodStats, ResourceLookup lookup, int markerSeverity) {
		this.testName = testName;
		this.methodStats = methodStats;
		this.lookup = lookup;
		this.markerSeverity = markerSeverity;
	}

	@Override
	public IResource associatedResource() throws CoreException {
		List<IResource> resources = lookup.findResourcesForClassName(testName);
		if (resources.isEmpty()) {
			return lookup.workspaceRoot();
		}
		return resources.get(0);
	}

	@Override
	public Map<String, Object> attributes() {
		Map<String, Object> markerAttributes = newLinkedHashMap();
		markerAttributes.put(SEVERITY, markerSeverity);
		markerAttributes.put(MESSAGE, buildMessage());

		return markerAttributes;
	}

	private String buildMessage() {
		return stripPackageName(testName) + "." + methodStats.methodName + " ran in " + methodStats.duration() + "ms";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SlowTestMarkerInfo) {
			SlowTestMarkerInfo other = (SlowTestMarkerInfo) obj;
			return testName.equals(other.testName) && methodStats.methodName.equals(other.methodStats.methodName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return testName.hashCode() ^ methodStats.methodName.hashCode();
	}

	@Override
	public String getTestName() {
		return testName;
	}

}
