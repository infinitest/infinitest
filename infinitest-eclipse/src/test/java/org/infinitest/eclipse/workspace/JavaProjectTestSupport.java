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

import static org.easymock.EasyMock.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public abstract class JavaProjectTestSupport
{
    public static final String PATH_TO_WORKSPACE = "/path/to/workspace";

    public static void expectProjectLocationFor(IJavaProject project, String projectName) throws JavaModelException
    {
        IResource projectResource = createMock(IResource.class);
        expect(projectResource.getLocation()).andReturn(new Path(PATH_TO_WORKSPACE + projectName)).anyTimes();
        expect(project.getCorrespondingResource()).andReturn(projectResource).anyTimes();
        replay(projectResource);
    }

    public static void outputLocationExpectation(IJavaProject project, String baseDir) throws JavaModelException
    {
        expect(project.getOutputLocation()).andReturn(new Path(baseDir + "/target/classes/")).anyTimes();
    }

    public static void expectClasspathFor(IJavaProject project, IClasspathEntry... entries) throws JavaModelException
    {
        expect(project.getResolvedClasspath(true)).andReturn(entries).anyTimes();
    }

    public static IJavaProject createMockProject(String projectPath, IClasspathEntry... entries)
                    throws JavaModelException, URISyntaxException
    {
        IJavaProject javaProject = createMock(IJavaProject.class);
        IProject project = createMock(IProject.class);
        expect(project.getLocationURI()).andReturn(new URI(projectPath));
        replay(project);
        expect(javaProject.getProject()).andReturn(project);
        expect(javaProject.getPath()).andReturn(new Path(projectPath)).anyTimes();
        expectProjectLocationFor(javaProject, projectPath);
        expectClasspathFor(javaProject, entries);
        outputLocationExpectation(javaProject, projectPath);
        return javaProject;
    }
}
