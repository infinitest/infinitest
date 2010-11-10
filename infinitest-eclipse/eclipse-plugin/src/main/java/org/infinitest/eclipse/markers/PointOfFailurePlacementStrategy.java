package org.infinitest.eclipse.markers;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;

public class PointOfFailurePlacementStrategy implements MarkerPlacementStrategy
{
    private final ResourceLookup lookup;

    public PointOfFailurePlacementStrategy(ResourceLookup lookup)
    {
        this.lookup = lookup;
    }

    public MarkerPlacement getPlacement(TestEvent event)
    {
        List<IResource> resources = lookup.findResourcesForClassName(event.getPointOfFailure().getClassName());
        if (resources.isEmpty())
            return null;
        return new MarkerPlacement(resources.get(0), event.getPointOfFailure().getLineNumber());
    }
}
