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
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

public class JavaProjectSetTest
{
    private IJavaProject project;
    private JavaProjectSet projectSet;

    @Before
    public void inContext()
    {
        URL resource = getClass().getResource("/emptyJar.jar");
        File externalJar = new File(resource.getPath());
        String externalJarPath = externalJar.getAbsolutePath();

        project = project("projectA").withJar("lib/someJarFile").andExportedJar("lib/someExportedJarFile")
                        .andExtenralJar(externalJarPath).andSourceDirectory("src", "srcbin").andDependsOn("projectB");
        ResourceFinder finder = mock(ResourceFinder.class);
        List<IJavaProject> projects = newArrayList(project);
        when(finder.getJavaProjects()).thenReturn(projects);
        projectSet = new JavaProjectSet(finder);
    }

    @Test
    public void shouldProvideOutputDirectoriesForProjectAsAbsolutePaths() throws JavaModelException
    {
        ArrayList<File> expectedDirectories = newArrayList(new File("/root/projectA/srcbin"), new File(
                        "/root/projectA/target/classes"));

        assertEquals(expectedDirectories, projectSet.outputDirectories(new ProjectFacade(project)));
    }
}
