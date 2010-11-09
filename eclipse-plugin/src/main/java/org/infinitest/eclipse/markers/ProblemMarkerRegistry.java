package org.infinitest.eclipse.markers;

import static org.infinitest.eclipse.PluginConstants.*;

import org.springframework.stereotype.Component;

@Component
public class ProblemMarkerRegistry extends GenericMarkerRegistry
{
    public ProblemMarkerRegistry()
    {
        super(PROBLEM_MARKER_ID);
    }
}
