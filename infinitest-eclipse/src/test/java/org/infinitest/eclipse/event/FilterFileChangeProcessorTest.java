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

import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.infinitest.eclipse.workspace.WorkspaceFacade;
import org.junit.jupiter.api.Test;

/**
 * @author gtoison
 *
 */
class FilterFileChangeProcessorTest {

	@Test
	void filterFileUpdate() throws CoreException {
		WorkspaceFacade workspace = mock(WorkspaceFacade.class);
		IResourceChangeEvent event = mock(IResourceChangeEvent.class);
		IResourceDelta delta = mock(IResourceDelta.class);
		IResourceDelta[] deltas = new IResourceDelta[] {mock(IResourceDelta.class)};
		IResourceDelta filterDelta = mock(IResourceDelta.class);
		IPath path = mock(IPath.class);
		
		when(event.getType()).thenReturn(IResourceChangeEvent.POST_BUILD);
		when(event.getDelta()).thenReturn(delta);
		
		when(delta.getAffectedChildren()).thenReturn(deltas);
		
		doAnswer(i -> {
			i.getArgument(0, IResourceDeltaVisitor.class).visit(filterDelta);
			return null;
		}).when(deltas[0]).accept(any());
		
		when(filterDelta.getFullPath()).thenReturn(path);
		when(path.toPortableString()).thenReturn(INFINITEST_FILTERS_FILE_NAME);
		
		FilterFileChangeProcessor processor = new FilterFileChangeProcessor(workspace);
		
		if (processor.canProcessEvent(event)) {
			processor.processEvent(event);
		}
		
		verify(workspace, times(1)).filterFileModified(anySet());
	}
}
