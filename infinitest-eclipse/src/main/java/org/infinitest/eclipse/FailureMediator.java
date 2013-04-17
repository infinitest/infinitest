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

import java.util.*;

import org.infinitest.*;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.testrunner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class FailureMediator implements FailureListListener {
	private final MarkerRegistry registry;
	private final ResourceLookup resourceLookup;

	@Autowired
	public FailureMediator(ProblemMarkerRegistry registry, ResourceLookup resourceLookup) {
		this.registry = registry;
		this.resourceLookup = resourceLookup;
	}

	public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		// Note that logging this may kill performance when you have a lot of
		// errors
		for (TestEvent testEvent : failuresAdded) {
			registry.addMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
		}

		for (TestEvent testEvent : failuresRemoved) {
			registry.removeMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
		}
	}

	public void failuresUpdated(Collection<TestEvent> updatedFailures) {
		for (TestEvent testEvent : updatedFailures) {
			registry.updateMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
		}
	}
}
