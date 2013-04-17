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

import static java.util.Arrays.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.sourcelookup.*;
import org.eclipse.jdt.core.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
class WorkspaceResourceFinder implements ResourceFinder {
	private final ISourceContainer sourceContainer;
	private IWorkspace workspace;

	@Autowired
	WorkspaceResourceFinder(ISourceContainer sourceContainer) {
		this.sourceContainer = sourceContainer;
	}

	@Autowired
	public void setWorkspace(IWorkspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public IProject getProject(URI projectUri) {
		try {
			for (IResource each : workspaceRoot().members()) {
				if (each.getLocationURI().equals(projectUri)) {
					return (IProject) each;
				}
			}
			throw new IllegalArgumentException("Could not find project for " + projectUri);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IResource findResourceForSourceFile(String sourceFile) {
		try {
			return findMostSpecific(sourceFile);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	private IResource findMostSpecific(String sourceFile) throws CoreException {
		IResource resolved = null;
		int shortestPath = Integer.MAX_VALUE;
		for (ISourceContainer container : sourceContainer.getSourceContainers()) {
			Object[] paths = container.findSourceElements(sourceFile);
			for (Object path : paths) {
				if (path instanceof IFile) {
					IFile file = (IFile) path;
					IPath p = file.getFullPath();
					if (p instanceof Path) {
						int count = ((Path) p).segmentCount();
						if (count < shortestPath) {
							shortestPath = count;
							resolved = file;
						}
					}
				}
			}
		}
		return resolved;
	}

	@Override
	public IWorkspaceRoot workspaceRoot() {
		return workspace.getRoot();
	}

	@Override
	public List<IJavaProject> getJavaProjects() {
		IWorkspaceRoot root = workspaceRoot();
		IJavaModel javaModel = JavaCore.create(root);
		try {
			return asList(javaModel.getJavaProjects());
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public File findFileFor(IPath path) {
		IWorkspaceRoot root = workspaceRoot();
		IResource member = root.findMember(path);
		if (member == null) {
			return null;
		}
		File file = new File(member.getLocationURI().getPath());
		if (file.exists()) {
			return file;
		}
		return null;
	}
}
