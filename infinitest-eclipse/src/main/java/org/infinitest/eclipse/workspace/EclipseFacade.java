/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2012
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.eclipse.workspace;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

public class EclipseFacade {
	public String[] computeDefaultRuntimeClassPath(IJavaProject project) throws CoreException {
		return JavaRuntime.computeDefaultRuntimeClassPath(project);
	}

	public IRuntimeClasspathEntry[] computeUnresolvedRuntimeClasspath(IJavaProject project) throws CoreException {
		return JavaRuntime.computeUnresolvedRuntimeClasspath(project);
	}

	public IClasspathContainer getClasspathContainer(IPath path, IJavaProject project) throws CoreException {
		return JavaCore.getClasspathContainer(path, project);
	}
}
