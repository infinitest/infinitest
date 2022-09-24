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

import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;
import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.AUTO_BUILD;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.workspace.WorkspaceFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenRespondingToBuildEvents extends ResourceEventSupport {
	private ClassFileChangeProcessor processor;
	private WorkspaceFacade workspace;

	@Before
	public void inContext() {
		workspace = mock(WorkspaceFacade.class);
		processor = new ClassFileChangeProcessor(workspace);
	}

	@Test
	public void shouldNotRespondToPreBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_BUILD, AUTO_BUILD, null);
		assertFalse(processor.canProcessEvent(event));

		verifyNoInteractions(workspace);		
	}

	@Test
	public void shouldNotUpdateIfClassesAreNotChanged() throws CoreException {
		processor.processEvent(emptyEvent());

		verifyNoInteractions(workspace);
	}

	@Test
	public void shouldRespondToPostBuildEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, null);
		assertTrue(processor.canProcessEvent(event));

		verifyNoInteractions(workspace);
	}

	@Test
	public void shouldRespondToPostChangeEvents() {
		IResourceChangeEvent event = new ResourceChangeEvent(this, POST_CHANGE, AUTO_BUILD, null);
		assertTrue(processor.canProcessEvent(event));

		verifyNoInteractions(workspace);
	}
	
	@Test
	public void shouldHandleRemovedTestClasses() throws CoreException {
		IResourceDelta delta = mock(IResourceDelta.class);
		IPath path = mock(IPath.class);
		
		when(delta.getKind()).thenReturn(IResourceDelta.REMOVED);
		when(delta.getAffectedChildren()).thenReturn(new IResourceDelta[] {delta});
		doAnswer(i -> {
			IResourceDeltaVisitor v = i.getArgument(0, IResourceDeltaVisitor.class);
			v.visit(delta);
			
			return null;
		}).when(delta).accept(any());
		when(delta.getFullPath()).thenReturn(path);
		when(path.toPortableString()).thenReturn("/target/test.class");
		
		IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_BUILD, AUTO_BUILD, delta);
		
		processor.processEvent(event);
		
		verify(workspace, times(1)).remove(anySet());
	}
}
