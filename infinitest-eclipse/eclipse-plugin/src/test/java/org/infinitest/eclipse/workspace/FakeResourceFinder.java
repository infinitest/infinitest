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

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.easymock.EasyMock.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

public class FakeResourceFinder implements ResourceFinder, ResourceLookup
{
    private final List<IJavaProject> projects;

    public FakeResourceFinder(List<IJavaProject> projects)
    {
        this.projects = projects;
    }

    public FakeResourceFinder(IJavaProject... projects)
    {
        this(newArrayList(projects));
    }

    public IResource findResourceForSourceFile(String sourceFile)
    {
        return createNiceMock(IResource.class);
    }

    public IProject getProject(URI projectUri)
    {
        return project(projectUri.getPath()).getProject();
    }

    public IProject findProjectResourceForClassName(String className)
    {
        return null;
    }

    public IResource findResourceFromClassName(String className)
    {
        return null;
    }

    public IWorkspaceRoot workspaceRoot()
    {
        return createNiceMock(IWorkspaceRoot.class);
    }

    public File findFileFor(IPath path)
    {
        return null;
    }

    public List<IJavaProject> getJavaProjects()
    {
        return projects;
    }

    public List<IResource> findResourcesForClassName(String className)
    {
        return emptyList();
    }
}
