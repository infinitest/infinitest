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

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class FakeProjectSet implements ProjectSet
{
    private final IJavaProject project;

    public FakeProjectSet(IJavaProject project)
    {
        this.project = project;
    }

    public ProjectFacade findProject(IPath path)
    {
        if (project.getPath().equals(path))
        {
            return new ProjectFacade(project);
        }
        throw new UnsupportedOperationException();
    }

    public boolean hasErrors() throws CoreException
    {
        throw new UnsupportedOperationException();
    }

    public List<ProjectFacade> projects()
    {
        throw new UnsupportedOperationException();
    }

    public List<File> outputDirectories(EclipseProject project) throws JavaModelException
    {
        throw new UnsupportedOperationException();
    }

}
