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

import static java.util.Arrays.*;
import static org.infinitest.eclipse.workspace.FakeResourceFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenPlacingMarkersInJavaClasses {
	private TestEvent event;
	private ResourceLookup lookup;
	private PointOfFailure pof;
	private IResource resource;
	private MarkerPlacementStrategy placementStrategy;

	@Before
	public void inContext() {
		event = mock(TestEvent.class);
		lookup = mock(ResourceLookup.class);
		pof = new PointOfFailure("com.myproject.MyClass", 70, "NullPointerException", "");
		resource = stubResource("/projectA/com/myproject/MyClass.java");
		placementStrategy = new PointOfFailurePlacementStrategy(lookup);
	}

	@Test
	public void shouldTryToUseClassNameFromPointOfFailure() {
		when(lookup.findResourcesForClassName("com.myproject.MyClass")).thenReturn(asList(resource));
		when(event.getPointOfFailure()).thenReturn(pof);

		MarkerPlacement placement = placementStrategy.getPlacement(event);

		assertSame(resource, placement.getResource());
		assertEquals(70, placement.getLineNumber());
	}

	@Test
	public void shouldReturnNullIfResourceCannotBeFound() {
		when(event.getPointOfFailure()).thenReturn(pof);
		when(lookup.findResourcesForClassName(anyString())).thenReturn(Collections.<IResource> emptyList());

		MarkerPlacement placement = placementStrategy.getPlacement(event);

		assertNull(placement);
	}
}
