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

import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public abstract class FakeResourceFactory
{
    public static IResource stubResource(String path)
    {
        IPath mockPath = mockPath(path);
        return mockResource(mockPath);
    }

    public static IResource mockResource(IPath path)
    {
        IResource resource = mock(IResource.class);
        when(resource.getLocation()).thenReturn(path);
        return resource;
    }

    public static IPath mockPath(String portableString)
    {
        IPath path = mock(IPath.class);
        when(path.toPortableString()).thenReturn(portableString);
        return path;
    }
}
