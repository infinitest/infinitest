package org.infinitest.eclipse.markers;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;

public class StackTracePlacementStrategy implements MarkerPlacementStrategy
{
    private final ResourceLookup lookup;

    public StackTracePlacementStrategy(ResourceLookup lookup)
    {
        this.lookup = lookup;
    }

    public MarkerPlacement getPlacement(TestEvent event)
    {
        for (StackTraceElement element : event.getStackTrace())
        {
            List<IResource> resources = lookup.findResourcesForClassName(element.getClassName());
            if (!resources.isEmpty())
            {
                return new MarkerPlacement(resources.get(0), element.getLineNumber());
            }
        }
        return null;
    }
}
