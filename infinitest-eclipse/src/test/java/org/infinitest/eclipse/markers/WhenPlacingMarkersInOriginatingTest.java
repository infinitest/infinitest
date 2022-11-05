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

import static java.util.Collections.singletonList;
import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenPlacingMarkersInOriginatingTest {
	private static final String TEST_NAME = "com.fakeco.TestFoo";

	private TestNamePlacementStrategy strategy;
	private TestEvent event;
	private IResource testResource;
	private ResourceLookup lookup;

	@BeforeEach
	void inContext() {
		testResource = mock(IResource.class);
		lookup = mock(ResourceLookup.class);
		strategy = new TestNamePlacementStrategy(lookup);
		event = eventWithError(new AssertionError());
	}

	@Test
	void shouldUseTestNameInEvent() {
		when(lookup.findResourcesForClassName(TEST_NAME)).thenReturn(singletonList(testResource));

		MarkerPlacement placement = strategy.getPlacement(event);

		assertSame(testResource, placement.getResource());
		assertEquals(0, placement.getLineNumber(), "Line zero indicates we don't know where to place the marker");
	}

	@Test
	void shouldReturnNullIfTestCannotBeFoundInWorkspace() {
		assertNull(strategy.getPlacement(event));
	}

	private static TestEvent eventWithError(Throwable error) {
		return new TestEvent(METHOD_FAILURE, "", TEST_NAME, "", error);
	}
}
