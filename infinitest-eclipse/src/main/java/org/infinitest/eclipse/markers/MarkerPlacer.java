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
