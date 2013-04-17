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

import static java.util.logging.Level.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.infinitest.eclipse.workspace.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
class CleanEventProcessor extends EclipseEventProcessor {
	private final CoreRegistry coreRegistry;
	private final ProjectSet projectSet;

	@Autowired
	CleanEventProcessor(CoreRegistry coreRegistry, ProjectSet projectSet) {
		super("Clearing Infinitest Indexes");
		this.coreRegistry = coreRegistry;
		this.projectSet = projectSet;
	}

	@Override
	public void processEvent(IResourceChangeEvent event) throws JavaModelException {
		cleanProjects(getDeltas(event));
	}

	private void cleanProjects(IResourceDelta[] projectResourceDeltas) {
		for (IResourceDelta projectDelta : projectResourceDeltas) {
			cleanProject(projectDelta);
		}
	}

	private void cleanProject(IResourceDelta projectResourceDelta) {
		IPath projectPath = projectResourceDelta.getResource().getFullPath();
		EclipseProject project = projectSet.findProject(projectPath);
		if (project == null) {
			log(WARNING, "Could not find project for resource " + projectPath);
		} else {
			coreRegistry.removeCore(project.getLocationURI());
		}
	}

	@Override
	public boolean canProcessEvent(IResourceChangeEvent event) {
		return (event.getBuildKind() == CLEAN_BUILD) && ((event.getType() & POST_BUILD) > 0);
	}

}
