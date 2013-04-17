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

import static com.google.common.collect.Lists.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenPlacingMarkersInOriginatingTest {
	private static final String TEST_NAME = "com.fakeco.TestFoo";

	private TestNamePlacementStrategy strategy;
	private TestEvent event;
	private IResource testResource;
	private ResourceLookup lookup;

	@Before
	public void inContext() {
		testResource = mock(IResource.class);
		lookup = mock(ResourceLookup.class);
		strategy = new TestNamePlacementStrategy(lookup);
		event = eventWithError(new AssertionError());
	}

	@Test
	public void shouldUseTestNameInEvent() {
		when(lookup.findResourcesForClassName(TEST_NAME)).thenReturn(newArrayList(testResource));

		MarkerPlacement placement = strategy.getPlacement(event);

		assertSame(testResource, placement.getResource());
		assertEquals("Line zero indicates we don't know where to place the marker", 0, placement.getLineNumber());
	}

	@Test
	public void shouldReturnNullIfTestCannotBeFoundInWorkspace() {
		assertNull(strategy.getPlacement(event));
	}

	private static TestEvent eventWithError(Throwable error) {
		return new TestEvent(METHOD_FAILURE, "", TEST_NAME, "", error);
	}
}
