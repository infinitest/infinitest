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
