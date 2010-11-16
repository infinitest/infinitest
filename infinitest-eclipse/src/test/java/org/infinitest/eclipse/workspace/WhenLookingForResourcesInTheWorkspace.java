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

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.junit.Test;

public class WhenLookingForResourcesInTheWorkspace
{
    @Test
    public void shouldHandleMissingResources()
    {
        ISourceContainer sourceContainer = mock(ISourceContainer.class);
        IWorkspace workspace = mock(IWorkspace.class);
        WorkspaceResourceFinder finder = new WorkspaceResourceFinder(sourceContainer);
        IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(workspace.getRoot()).thenReturn(workspaceRoot);
        finder.setWorkspace(workspace);
        IPath path = mock(IPath.class);
        assertNull(finder.findFileFor(path));
    }
}
