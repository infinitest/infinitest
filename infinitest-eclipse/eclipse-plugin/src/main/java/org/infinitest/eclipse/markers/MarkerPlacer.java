package org.infinitest.eclipse.markers;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;

import java.util.List;

import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MarkerPlacer
{
    private final List<MarkerPlacementStrategy> placementStrategyChain = newArrayList();

    @Autowired
    public MarkerPlacer(ResourceLookup lookup)
    {
        addLink(new PointOfFailurePlacementStrategy(lookup));
        addLink(new StackTracePlacementStrategy(lookup));
        addLink(new TestNamePlacementStrategy(lookup));
        addLink(new WorkspaceFallbackPlacementStrategy(lookup.workspaceRoot()));
    }

    void addLink(MarkerPlacementStrategy link)
    {
        placementStrategyChain.add(link);
    }

    public MarkerPlacement findPlacement(TestEvent event)
    {
        for (MarkerPlacementStrategy each : placementStrategyChain)
        {
            MarkerPlacement placement = each.getPlacement(event);
            if (placement != null)
            {
                return placement;
            }
        }
        return null;
    }

    public List<MarkerPlacementStrategy> getLinks()
    {
        return unmodifiableList(placementStrategyChain);
    }
}
