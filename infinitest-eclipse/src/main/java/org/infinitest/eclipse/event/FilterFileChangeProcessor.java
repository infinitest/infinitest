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
import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.infinitest.environment.FileCustomJvmArgumentReader.INFINITEST_ARGS_FILE_NAME;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.WorkspaceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Triggers a tests run when a filter file or an args file is updated, created, deleted
 * 
 * @author gtoison
 */
@Component
class FilterFileChangeProcessor extends EclipseEventProcessor {
	private final WorkspaceFacade workspace;

	@Autowired
	FilterFileChangeProcessor(WorkspaceFacade workspace) {
		super("Filter file change processor");
		this.workspace = workspace;
	}

	@Override
	public boolean canProcessEvent(IResourceChangeEvent event) {
		return (event.getType() & POST_BUILD) > 0 && event.getDelta() != null;
	}

	@Override
	public void processEvent(IResourceChangeEvent event) throws CoreException {
		FilterFileChangeProcessorVisitor visitor = new FilterFileChangeProcessorVisitor();
		
		findModifiedResources(visitor, getDeltas(event));
		
		if (!visitor.filterFileModified.isEmpty()) {
			workspace.filterFileModified(visitor.filterFileModified);
		}
	}

	private void findModifiedResources(FilterFileChangeProcessorVisitor visitor, IResourceDelta... deltas) throws CoreException {
		for (IResourceDelta delta : deltas) {
			delta.accept(visitor);
		}
	}
	
	public static class FilterFileChangeProcessorVisitor implements IResourceDeltaVisitor {
		private Set<IResource> filterFileModified = new HashSet<>();
		
		@Override
		public boolean visit(IResourceDelta d) throws CoreException {
			if (isFilterFile(d)) {
				filterFileModified.add(d.getResource());
			}
			
			return true;
		}
		private boolean isFilterFile(IResourceDelta delta) {
			String path = delta.getFullPath().toPortableString();
			return path.endsWith(INFINITEST_FILTERS_FILE_NAME) || path.endsWith(INFINITEST_ARGS_FILE_NAME);
		}
	}
}
