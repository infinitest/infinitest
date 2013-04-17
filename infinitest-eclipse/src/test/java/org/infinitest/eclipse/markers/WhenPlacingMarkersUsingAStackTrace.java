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
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.junit.*;

public class WhenPlacingMarkersUsingAStackTrace {
	private static final int SECOND_ELEMENT_LINE_NUMBER = 25;
	private StackTracePlacementStrategy strategy;
	private Throwable error;
	private IResource mockResource;

	@Before
	public void inContext() {
		ResourceLookup lookup = mock(ResourceLookup.class);
		mockResource = mock(IResource.class);
		when(lookup.findResourcesForClassName("com.myco.Foo")).thenReturn(asList(mockResource));
		strategy = new StackTracePlacementStrategy(lookup);
		error = mock(Throwable.class);
	}

	@Test
	public void shouldFindTheFirstClassInTheWorkspace() {
		expectStackTraceWithClass("com.myco.Foo");

		MarkerPlacement placement = strategy.getPlacement(eventWithError(error));

		assertEquals(SECOND_ELEMENT_LINE_NUMBER, placement.getLineNumber());
		assertSame(mockResource, placement.getResource());
	}

	@Test
	public void shouldReturnNullIfNoClassesAreInTheWorkspace() {
		expectStackTraceWithClass("com.myco.Baz");

		assertNull(strategy.getPlacement(eventWithError(error)));
	}

	private void expectStackTraceWithClass(String secondElementClass) {
		StackTraceElement[] stackTrace = new StackTraceElement[] { new StackTraceElement("com.myco.Bar", "method", "file", 10), new StackTraceElement(secondElementClass, "method", "file", SECOND_ELEMENT_LINE_NUMBER) };
		when(error.getStackTrace()).thenReturn(stackTrace);
	}

	private static TestEvent eventWithError(Throwable error) {
		return new TestEvent(METHOD_FAILURE, "", "", "", error);
	}
}
