package org.infinitest.eclipse.markers;

import static org.infinitest.eclipse.PluginConstants.*;

import org.springframework.stereotype.Component;

@Component
public class SlowMarkerRegistry extends GenericMarkerRegistry
{
    public SlowMarkerRegistry()
    {
        super(SLOW_MARKER_ID);
    }
}
