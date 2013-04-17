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
package org.infinitest.eclipse.resolution;

import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.core.internal.resources.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.*;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {
	public IMarkerResolution[] getResolutions(IMarker marker) {
		if (hasResolutions(marker)) {
			return new IMarkerResolution[] { new ErrorViewerResolution(getTestAndMethodName(marker)) };
		}
		return new IMarkerResolution[0];
	}

	private String getTestAndMethodName(IMarker marker) {
		try {
			Object attribute = marker.getAttribute(TEST_NAME_ATTRIBUTE);
			if (attribute == null) {
				return null;
			}
			String testName = attribute.toString();
			Object methodName = marker.getAttribute(METHOD_NAME_ATTRIBUTE);
			return stripPackageName(testName) + "." + methodName;
		} catch (ResourceException e) {
			return null;
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasResolutions(IMarker marker) {
		return getTestAndMethodName(marker) != null;
	}
}
