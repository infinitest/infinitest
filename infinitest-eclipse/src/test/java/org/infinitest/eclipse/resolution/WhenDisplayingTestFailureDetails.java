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

import static com.google.common.collect.Iterables.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenDisplayingTestFailureDetails {
	protected Collection<StackTraceElement> actualStackTrace;
	private ErrorViewerResolution resolution;
	protected String actualMessage;

	@BeforeEach
	void inContext() {
		actualStackTrace = new ArrayList<>();
		resolution = new ErrorViewerResolution("TestName.methodName") {
			@Override
			protected void createStackViewWith(List<StackTraceElement> trace, String message) {
				actualStackTrace = trace;
				actualMessage = message;
			}
		};
	}

	@Test
	void shouldUseTestAndMethodNameInLabel() {
		assertEquals("TestName.methodName failing (see details)", resolution.getLabel());
	}

	@Test
	void printStackTraceWithSourceFileLinksUsingInternalJavaStackTraceConsole() throws Exception {
		IMarker marker = mock(IMarker.class);
		StackTraceElement element = new StackTraceElement("", "", "", 0);
		Object pickledStackTrace = pickle(new StackTraceElement[] { element });
		when(marker.getAttribute(PICKLED_STACK_TRACE_ATTRIBUTE)).thenReturn(pickledStackTrace);
		when(marker.getAttribute(MESSAGE)).thenReturn("message");

		resolution.run(marker);
		assertEquals(element, getOnlyElement(actualStackTrace));
		assertEquals("message", actualMessage);
	}
}
