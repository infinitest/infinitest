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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

public class WhenExtractingSettingsFromAJavaProject
{
    private ProjectFacade facade;
    private IJavaProject project;
    private IPath path;

    @Before
    public void inContext() throws JavaModelException
    {
        project = mock(IJavaProject.class);
        path = mock(IPath.class);
        when(project.getOutputLocation()).thenReturn(path);
        facade = new ProjectFacade(project);
    }

    @Test
    public void shouldProvideDefaultOutputDirectory()
    {
        assertSame(path, facade.getDefaultOutputLocation());
    }
}
