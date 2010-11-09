package org.infinitest.eclipse.markers;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.infinitest.testrunner.TestEvent;

public class WorkspaceFallbackPlacementStrategy implements MarkerPlacementStrategy
{
    private final IWorkspaceRoot root;

    public WorkspaceFallbackPlacementStrategy(IWorkspaceRoot root)
    {
        this.root = root;
    }

    public MarkerPlacement getPlacement(TestEvent event)
    {
        return new MarkerPlacement(root, 0);
    }

}
