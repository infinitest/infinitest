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
package org.infinitest.eclipse.event;

import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.eclipse.core.internal.events.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenRespondingToBuildEvents extends ResourceEventSupport {
	private ClassFileChangeProcessor processor;
	private WorkspaceFacade workspace;

	@BeforeEach
	void inContext() {
		workspace = mock(WorkspaceFacade.class);
		processor = new ClassFileChangeProcessor(workspace);
	}

	@AfterEach
	void verifyWorkspace() {
		verifyNoInteractions(workspace);
	}

	@Test
	void shouldNotRespondToPreBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_BUILD, AUTO_BUILD, null);
		assertFalse(processor.canProcessEvent(event));
	}

	@Test
	void shouldNotUpdateIfClassesAreNotChanged() throws CoreException {
		processor.processEvent(emptyEvent());
	}

	@Test
	void shouldRespondToPostBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, null);
		assertTrue(processor.canProcessEvent(event));
	}

	@Test
	void shouldRespondToPostChangeEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_CHANGE, AUTO_BUILD, null);
		assertTrue(processor.canProcessEvent(event));
	}
}
