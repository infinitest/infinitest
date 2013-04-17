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
