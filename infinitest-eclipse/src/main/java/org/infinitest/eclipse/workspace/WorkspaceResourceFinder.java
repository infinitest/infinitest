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

	public IWorkspaceRoot workspaceRoot() {
		return workspace.getRoot();
	}

	public List<IJavaProject> getJavaProjects() {
		IWorkspaceRoot root = workspaceRoot();
		IJavaModel javaModel = JavaCore.create(root);
		try {
			return asList(javaModel.getJavaProjects());
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

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
