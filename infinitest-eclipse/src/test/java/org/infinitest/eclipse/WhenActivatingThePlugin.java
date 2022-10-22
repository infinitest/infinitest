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

import static org.mockito.Mockito.*;

import org.eclipse.core.resources.*;
import org.infinitest.*;
import org.infinitest.eclipse.trim.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenActivatingThePlugin {
	private IWorkspace workspace;
	private InfinitestActivationController controller;
	private IResourceChangeListener coreUpdateNotifier;
	private VisualStatusRegistry visualStatusRegistry;
	private EventQueue eventQueue;
	private NamedRunnable markerClearingRunnable;

	@BeforeEach
	final void inContext() {
		workspace = mock(IWorkspace.class);
		coreUpdateNotifier = mock(IResourceChangeListener.class);
		visualStatusRegistry = mock(VisualStatusRegistry.class);
		eventQueue = mock(EventQueue.class);
		markerClearingRunnable = mock(NamedRunnable.class);

		controller = new InfinitestActivationController();
		controller.setVisualStatusRegistry(visualStatusRegistry);
		controller.setWorkspace(workspace);
		controller.setUpdateNotifier(coreUpdateNotifier);
		controller.setEventQueue(eventQueue);
		controller.setMarkerClearingRunnable(markerClearingRunnable);
	}

	@Test
	void shouldNotAddTwoListenerIfPluginIsEnabledTwice() {
		controller.enable();
		controller.enable();

		verify(workspace).addResourceChangeListener(coreUpdateNotifier);
	}

	@Test
	void shouldRemoveListenersWhenDisabled() {
		controller.disable();
		verify(workspace).removeResourceChangeListener(coreUpdateNotifier);
	}

	@Test
	void shouldClearMarkersOnEventQueueWhenDisabled() {
		controller.disable();
		verify(eventQueue).pushNamed(markerClearingRunnable);
	}
}
