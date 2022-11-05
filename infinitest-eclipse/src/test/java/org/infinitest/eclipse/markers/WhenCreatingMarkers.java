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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IMarker.SEVERITY;
import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.infinitest.eclipse.PluginConstants.PROBLEM_MARKER_ID;
import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IMarker;
import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.infinitest.testrunner.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenCreatingMarkers {
	private GenericMarkerRegistry registry;
	private AssertionError error;

	@BeforeEach
	void inContext() {
		error = new AssertionError();
		registry = new GenericMarkerRegistry(PROBLEM_MARKER_ID);
	}

	@Test
	void shouldNeverAllowTwoMarkersForTheSameTest() {
		// This check is defensive. Although you'd think the problem marker for
		// a test
		// would be removed before we try to add another one, we really want to
		// make
		// sure that we don't have any lingering markers

		addMarker(methodFailed("message", "testName", "methodName", new Throwable("Fail")));
		addMarker(methodFailed("anotherMessage", "testName", "methodName", new Throwable("SuperFail")));

		MarkerInfo marker = getOnlyElement(registry.getMarkers());
		assertThat(getAttribute(marker, MESSAGE).toString()).contains("anotherMessage");
		assertEquals(SEVERITY_ERROR, getAttribute(marker, SEVERITY));
	}

	@Test
	void shouldCreateNewMarkersForNewPointsOfFailure() {
		addMarker(failureEvent("my failure message"));

		MarkerInfo marker = getOnlyElement(registry.getMarkers());
		assertEquals("AssertionError (my failure message) in TestName.methodName", getAttribute(marker, MESSAGE));
		assertEquals(SEVERITY_ERROR, getAttribute(marker, SEVERITY));
	}

	@Test
	void shouldGenerateMessageForFailuresWithStringifiedNullMessage() {
		addMarker(failureEvent("null"));

		MarkerInfo marker = getOnlyElement(registry.getMarkers());
		assertEquals(error.getClass().getSimpleName() + " in " + "TestName.methodName", getAttribute(marker, MESSAGE));
	}

	@Test
	void shouldGenerateMessageForFailuresWithBlankMessage() {
		addMarker(failureEvent(""));

		MarkerInfo marker = getOnlyElement(registry.getMarkers());
		assertEquals(error.getClass().getSimpleName() + " in " + "TestName.methodName", getAttribute(marker, MESSAGE));
	}

	@Test
	void shouldRemoveLineBreaksFromErrorMessages() {
		Throwable throwable = new Exception("message with\nline break");
		ProblemMarkerInfo info = new ProblemMarkerInfo(methodFailed("testName", "methodName", throwable), new FakeResourceFinder());
		
		String message = (String) info.attributes().get(IMarker.MESSAGE);
		assertThat(message).contains("message with line break");
	}

	@Test
	void shouldRemoveCarraigeReturnsFromErrorMessages() {
		Throwable throwable = new Exception("message with\rline break");
		ProblemMarkerInfo info = new ProblemMarkerInfo(methodFailed("testName", "methodName", throwable), new FakeResourceFinder());
		
		String message = (String) info.attributes().get(IMarker.MESSAGE);
		assertThat(message).contains("message with line break");
	}

	private Object getAttribute(MarkerInfo marker, String message) {
		return marker.attributes().get(message);
	}

	@Test
	void shouldIgnoredRemovedMarkersThatDontExist() {
		assertDoesNotThrow(() -> registry.removeMarker(mock(MarkerInfo.class)));
	}

	@Test
	void shouldRemoveMarkersWhenTestPasses() {
		TestEvent event = failureEvent("my failure message");
		addMarker(event);
		registry.removeMarker(new FakeProblemMarkerInfo(event));

		assertThat(registry.getMarkers()).isEmpty();
	}

	@Test
	void shouldOnlyAddOneMarkerIfSameEventIsAddedTwice() {
		addMarker(failureEvent("my failure message"));
		addMarker(failureEvent("my failure message"));

		assertEquals(1, registry.getMarkers().size());
		MarkerInfo marker = getOnlyElement(registry.getMarkers());
		assertThat(getAttribute(marker, MESSAGE).toString()).contains("my failure message");
	}

	@Test
	void canRemoveAllMarkersForASpecifiedTestCase() {
		addMarker(failureEvent("my failure message"));
		registry.removeMarkers("NotATest");
		assertThat(registry.getMarkers()).isNotEmpty();

		registry.removeMarkers("com.fake.TestName");
		assertThat(registry.getMarkers()).isEmpty();
	}

	@Test
	void canClearAllMarkers() {
		addMarker(failureEvent("my failure message"));
		addMarker(failureEvent("another failure message"));
		addMarker(failureEvent("yet failure message"));
		registry.clear();

		assertThat(registry.getMarkers()).isEmpty();
	}

	private void addMarker(TestEvent event) {
		registry.addMarker(new FakeProblemMarkerInfo(event));
	}

	private TestEvent failureEvent(String failureMessage) {
		return methodFailed(failureMessage, "com.fake.TestName", "methodName", error);
	}
}
