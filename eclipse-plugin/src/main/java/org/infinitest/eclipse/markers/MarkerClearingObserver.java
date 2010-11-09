package org.infinitest.eclipse.markers;

import org.infinitest.eclipse.UpdateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MarkerClearingObserver implements UpdateListener
{
    private final MarkerRegistry registry;

    @Autowired
    public MarkerClearingObserver(SlowMarkerRegistry registry)
    {
        this.registry = registry;
    }

    public void projectsUpdated()
    {
        registry.clear();
    }
}