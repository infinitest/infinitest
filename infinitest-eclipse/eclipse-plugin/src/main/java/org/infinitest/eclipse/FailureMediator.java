package org.infinitest.eclipse;

import java.util.Collection;

import org.infinitest.FailureListListener;
import org.infinitest.eclipse.markers.MarkerRegistry;
import org.infinitest.eclipse.markers.ProblemMarkerInfo;
import org.infinitest.eclipse.markers.ProblemMarkerRegistry;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FailureMediator implements FailureListListener
{
    private final MarkerRegistry registry;
    private final ResourceLookup resourceLookup;

    @Autowired
    public FailureMediator(ProblemMarkerRegistry registry, ResourceLookup resourceLookup)
    {
        this.registry = registry;
        this.resourceLookup = resourceLookup;
    }

    public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
    {
        // Note that logging this may kill performance when you have a lot of errors
        for (TestEvent testEvent : failuresAdded)
        {
            registry.addMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
        }

        for (TestEvent testEvent : failuresRemoved)
        {
            registry.removeMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
        }
    }

    public void failuresUpdated(Collection<TestEvent> updatedFailures)
    {
        for (TestEvent testEvent : updatedFailures)
        {
            registry.updateMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
        }
    }
}
