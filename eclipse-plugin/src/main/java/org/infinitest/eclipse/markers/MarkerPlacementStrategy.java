package org.infinitest.eclipse.markers;

import org.infinitest.testrunner.TestEvent;

public interface MarkerPlacementStrategy
{
    MarkerPlacement getPlacement(TestEvent event);
}
