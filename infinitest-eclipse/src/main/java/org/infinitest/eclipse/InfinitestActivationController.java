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

import org.eclipse.core.resources.*;
import org.infinitest.*;
import org.infinitest.eclipse.trim.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class InfinitestActivationController implements PluginActivationController {
	private boolean pluginEnabled;
	private VisualStatusRegistry visualStatusRegistry;
	private EventQueue eventQueue;
	private NamedRunnable markerClearingRunnable;
	private IResourceChangeListener updateNotifier;
	private IWorkspace workspace;

	@Autowired
	public void setMarkerClearingRunnable(NamedRunnable markerClearingRunnable) {
		this.markerClearingRunnable = markerClearingRunnable;
	}

	@Autowired
	public void setVisualStatusRegistry(VisualStatusRegistry visualStatusRegistry) {
		this.visualStatusRegistry = visualStatusRegistry;
	}

	@Autowired
	public void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	@Autowired
	public void setWorkspace(IWorkspace workspace) {
		this.workspace = workspace;
	}

	@Autowired
	public void setUpdateNotifier(IResourceChangeListener updateNotifier) {
		this.updateNotifier = updateNotifier;
	}

	@Override
	public void enable() {
		if (!pluginEnabled) {
			attachListener();
		}
	}

	@Override
	public void disable() {
		workspace.removeResourceChangeListener(updateNotifier);
		pluginEnabled = false;

		eventQueue.pushNamed(markerClearingRunnable);
	}

	private void attachListener() {
		pluginEnabled = true;
		// calling addResourceChangeListener() without the event type second argument is equivalent to
		// listening only to: PRE_CLOSE, PRE_DELETE and POST_CHANGE

		// Here we set the second argument to the event types we are interested in:
		// - POST_BUILD to capture the changes in .class files after a build (so we get all the changes at once)
		// - PRE_CLOSE to be notified when a project is closed, so we can clean up the failed tests
		workspace.addResourceChangeListener(updateNotifier, IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.PRE_CLOSE);
	}

	@Override
	public void attachVisualStatus(VisualStatus status) {
		visualStatusRegistry.updateVisualStatus(status);
	}
}
