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
package org.infinitest.eclipse.workspace;

import static org.infinitest.util.InfinitestUtils.*;

import java.net.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.eclipse.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
class InfinitestCoreRegistry implements CoreRegistry {
	private final Map<URI, InfinitestCore> coreMap = new HashMap<>();
	private final List<CoreLifecycleListener> listeners = new ArrayList<>();

	@Autowired
	InfinitestCoreRegistry(CoreLifecycleListener... listeners) {
		for (CoreLifecycleListener each : listeners) {
			addLifecycleListener(each);
		}
	}

	@Override
	public void addCore(URI projectUri, InfinitestCore core) {
		fireAddedEvent(core);
		coreMap.put(projectUri, core);
	}

	@Override
	public InfinitestCore getCore(URI projectUri) {
		return coreMap.get(projectUri);
	}

	@Override
	public void removeCore(URI projectUri) {
		InfinitestCore core = coreMap.remove(projectUri);
		if (core != null) {
			fireRemovedEvent(core);
			log("Removing core " + core.getName());
		}
	}

	private void fireRemovedEvent(InfinitestCore core) {
		for (CoreLifecycleListener each : listeners) {
			each.coreRemoved(core);
		}
	}

	private void fireAddedEvent(InfinitestCore core) {
		for (CoreLifecycleListener each : listeners) {
			each.coreCreated(core);
		}
	}

	public int indexedCoreCount() {
		return coreMap.size();
	}

	@Override
	public void addLifecycleListener(CoreLifecycleListener listener) {
		listeners.add(listener);
	}
}
