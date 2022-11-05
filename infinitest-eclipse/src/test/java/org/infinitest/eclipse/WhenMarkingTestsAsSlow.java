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
import static org.eclipse.core.resources.IMarker.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.infinitest.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenMarkingTestsAsSlow extends EqualityTestSupport {
	private static final String TEST_NAME = "com.foo.TestName";
	private SlowTestMarkerInfo marker;
	private ResourceLookup resourceLookup;

	@BeforeEach
	void inContext() {
		resourceLookup = mock(ResourceLookup.class);
		MethodStats stats = new MethodStats("shouldRunSlowly");
		stats.stop(5000);
		marker = new SlowTestMarkerInfo(TEST_NAME, stats, resourceLookup);
	}

	@Test
	void shouldPlaceMarkerInSlowTest() throws CoreException {
		IResource resource = mock(IResource.class);
		when(resourceLookup.findResourcesForClassName(TEST_NAME)).thenReturn(asList(resource));
		assertSame(resource, marker.associatedResource());
	}

	@Test
	void shouldFallbackToWorkspaceInSlowTest() throws CoreException {
		when(resourceLookup.findResourcesForClassName(TEST_NAME)).thenReturn(new ArrayList<IResource>());
		IWorkspaceRoot workspaceRootResource = mock(IWorkspaceRoot.class);
		when(resourceLookup.workspaceRoot()).thenReturn(workspaceRootResource);
		assertSame(workspaceRootResource, marker.associatedResource());
	}

	@Test
	void shouldUseWarningSeverity() {
		assertEquals(SEVERITY_WARNING, marker.attributes().get(SEVERITY));
	}

	@Test
	void shouldIndicateTestAndMethodName() {
		assertEquals("TestName.shouldRunSlowly ran in 5000ms", marker.attributes().get(MESSAGE));
	}

	@Override
	protected Object createEqualInstance() {
		return new SlowTestMarkerInfo(TEST_NAME, new MethodStats("shouldRunSlowly"), resourceLookup);
	}

	@Override
	protected Object createUnequalInstance() {
		return new SlowTestMarkerInfo(TEST_NAME, new MethodStats("shouldRunQuickly"), resourceLookup);
	}
}
