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

import static com.google.common.collect.Lists.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.junit.*;
import org.mockito.*;

public class FailureMediatorTest {
	private FailureMediator mediator;
	private ProblemMarkerRegistry registry;
	private TestEvent event1;
	private TestEvent event2;
	private TestEvent event3;
	private FakeResourceFinder finder;

	@Before
	public void inContext() {
		registry = Mockito.mock(ProblemMarkerRegistry.class);
		finder = new FakeResourceFinder();
		mediator = new FailureMediator(registry, finder);
		event1 = event("method1");
		event2 = event("method1");
		event3 = event("method1");
	}

	@Test
	public void shouldRelayAddRemoveEvents() {
		Collection<TestEvent> failuresAdded = newArrayList(event1, event2);
		Collection<TestEvent> failuresRemoved = newArrayList(event3);

		mediator.failureListChanged(failuresAdded, failuresRemoved);

		verify(registry, times(2)).addMarker(any(MarkerInfo.class));
		ProblemMarkerInfo markerInfo = new ProblemMarkerInfo(event3, finder);
		verify(registry).removeMarker(markerInfo);
	}

	@Test
	public void shouldRelayUpdateEvents() {
		Collection<TestEvent> updatedFailures = newArrayList(event1, event2);

		mediator.failuresUpdated(updatedFailures);

		verify(registry, times(2)).updateMarker(any(MarkerInfo.class));
	}

	private TestEvent event(String methodName) {
		return methodFailed("testClass", methodName, new AssertionError());
	}
}
