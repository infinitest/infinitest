package org.infinitest.eclipse;

import static java.util.Arrays.*;

import java.util.List;

import org.infinitest.NamedRunnable;
import org.infinitest.eclipse.markers.MarkerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MarkerClearingRunnable extends NamedRunnable
{
    private List<MarkerRegistry> registries;

    @Autowired
    public MarkerClearingRunnable(MarkerRegistry... markerRegistries)
    {
        super("Clearing markers");
        registries = asList(markerRegistries);
        if (registries.isEmpty())
            throw new IllegalArgumentException("No marker registries to clear! Expected at least two." + registries);
    }

    public void run()
    {
        for (MarkerRegistry each : registries)
            each.clear();
    }
}