/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
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

import static org.mockito.Mockito.*;

import java.net.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

public abstract class JavaProjectTestSupport {
	public static final String PATH_TO_WORKSPACE = "/path/to/workspace";

	public static void expectProjectLocationFor(IJavaProject project, String projectName) throws JavaModelException {
		IResource projectResource = mock(IResource.class);
		when(projectResource.getLocation()).thenReturn(new Path(PATH_TO_WORKSPACE + projectName));
		when(project.getCorrespondingResource()).thenReturn(projectResource);
	}

	public static void outputLocationExpectation(IJavaProject project, String baseDir) throws JavaModelException {
		when(project.getOutputLocation()).thenReturn(new Path(baseDir + "/target/classes/"));
	}

	public static void expectClasspathFor(IJavaProject project, IClasspathEntry... entries) throws JavaModelException {
		when(project.getResolvedClasspath(true)).thenReturn(entries);
	}

	public static IJavaProject createMockProject(String projectPath, IClasspathEntry... entries) throws JavaModelException, URISyntaxException {
		IJavaProject javaProject = mock(IJavaProject.class);
		IProject project = mock(IProject.class);
		when(project.getLocationURI()).thenReturn(new URI(projectPath));
		when(javaProject.getProject()).thenReturn(project);
		when(javaProject.getPath()).thenReturn(new Path(projectPath));
		expectProjectLocationFor(javaProject, projectPath);
		expectClasspathFor(javaProject, entries);
		outputLocationExpectation(javaProject, projectPath);
		return javaProject;
	}
}
