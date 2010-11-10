package org.infinitest.eclipse.markers;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;

public class TestNamePlacementStrategy implements MarkerPlacementStrategy
{
    private final ResourceLookup lookup;

    public TestNamePlacementStrategy(ResourceLookup lookup)
    {
        this.lookup = lookup;
    }

    public MarkerPlacement getPlacement(TestEvent event)
    {
        List<IResource> resources = lookup.findResourcesForClassName(event.getTestName());
        if (resources.isEmpty())
            return null;
        return new MarkerPlacement(resources.get(0), 0);
    }
}
