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

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;

public class FakeResourceFinder implements ResourceFinder, ResourceLookup {
	private final List<IJavaProject> projects;

	public FakeResourceFinder(List<IJavaProject> projects) {
		this.projects = projects;
	}

	public FakeResourceFinder(IJavaProject... projects) {
		this(asList(projects));
	}

	@Override
	public IResource findResourceForSourceFile(String sourceFile) {
		return mock(IResource.class);
	}

	@Override
	public IProject getProject(URI projectUri) {
		return project(projectUri.getPath()).getProject();
	}

	public IProject findProjectResourceForClassName(String className) {
		return null;
	}

	public IResource findResourceFromClassName(String className) {
		return null;
	}

	@Override
	public IWorkspaceRoot workspaceRoot() {
		return mock(IWorkspaceRoot.class);
	}

	@Override
	public File findFileFor(IPath path) {
		return null;
	}

	@Override
	public List<IJavaProject> getJavaProjects() {
		return projects;
	}

	@Override
	public List<IResource> findResourcesForClassName(String className) {
		return emptyList();
	}
}
