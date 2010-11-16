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

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

@Component
class JavaProjectSet implements ProjectSet
{
    private final ResourceFinder finder;

    @Autowired
    public JavaProjectSet(ResourceFinder finder)
    {
        this.finder = finder;
    }

    public ProjectFacade findProject(IPath path)
    {
        for (IJavaProject project : openProjects())
        {
            if (project.getPath().equals(path))
            {
                return createProjectFacade(project);
            }
        }

        return null;
    }

    protected ProjectFacade createProjectFacade(IJavaProject project)
    {
        return new ProjectFacade(project);
    }

    File workingDirectory(IJavaProject project) throws JavaModelException
    {
        return project.getCorrespondingResource().getLocation().toFile();
    }

    public List<ProjectFacade> projects()
    {
        List<ProjectFacade> projects = newArrayList();
        for (IJavaProject project : openProjects())
        {
            projects.add(new ProjectFacade(project));
        }
        return projects;
    }

    public boolean hasErrors() throws CoreException
    {
        for (ProjectFacade project : projects())
        {
            if (project.hasErrors())
            {
                return true;
            }
        }
        return false;
    }

    private Iterable<IJavaProject> openProjects()
    {
        return filter(finder.getJavaProjects(), new Predicate<IJavaProject>()
        {
            public boolean apply(IJavaProject input)
            {
                return input.isOpen();
            }
        });
    }

    /**
     * Return a list of Files that represent of all of the project's output directories. This
     * includes the project's default output directory, as well as any custom output directories.
     * All Files are absolute.
     * 
     * @throws JavaModelException
     */
    public List<File> outputDirectories(EclipseProject project) throws JavaModelException
    {
        // I suspect there's something, somewhere in the Eclipse SDK that will find this for us.
        List<File> outputDirectories = customOutputDirectoriesFor(project);

        outputDirectories.add(absoluteFile(project.getDefaultOutputLocation()));

        return outputDirectories;
    }

    private List<File> customOutputDirectoriesFor(EclipseProject project)
    {
        List<File> outputDirectories = Lists.newArrayList();
        for (IClasspathEntry each : project.getClasspathEntries())
        {
            IPath outputLocation = each.getOutputLocation();
            if (outputLocation != null)
            {
                File file = absoluteFile(outputLocation);
                outputDirectories.add(file);
            }
        }
        return outputDirectories;
    }

    private File absoluteFile(IPath path)
    {
        File file = new File(path.toPortableString());
        if (file.exists())
        {
            return file;
        }

        file = finder.findFileFor(path);
        if (file != null)
        {
            return file;
        }

        String jarPath = relativePath(path);

        Path projectPath = new Path("/" + path.segment(0));
        ProjectFacade project = findProject(projectPath);
        return new File(project.workingDirectory(), jarPath);
    }

    private String relativePath(IPath path)
    {
        return path.removeFirstSegments(1).toPortableString();
    }
}
