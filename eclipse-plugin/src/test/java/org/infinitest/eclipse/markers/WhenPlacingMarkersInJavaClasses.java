package org.infinitest.eclipse.markers;

import static java.util.Arrays.*;
import static org.infinitest.eclipse.workspace.FakeResourceFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersInJavaClasses
{
    private TestEvent event;
    private ResourceLookup lookup;
    private PointOfFailure pof;
    private IResource resource;
    private MarkerPlacementStrategy placementStrategy;

    @Before
    public void inContext()
    {
        event = mock(TestEvent.class);
        lookup = mock(ResourceLookup.class);
        pof = new PointOfFailure("com.myproject.MyClass", 70, "NullPointerException", "");
        resource = stubResource("/projectA/com/myproject/MyClass.java");
        placementStrategy = new PointOfFailurePlacementStrategy(lookup);
    }

    @Test
    public void shouldTryToUseClassNameFromPointOfFailure()
    {
        when(lookup.findResourcesForClassName("com.myproject.MyClass")).thenReturn(asList(resource));
        when(event.getPointOfFailure()).thenReturn(pof);

        MarkerPlacement placement = placementStrategy.getPlacement(event);

        assertSame(resource, placement.getResource());
        assertEquals(70, placement.getLineNumber());
    }

    @Test
    public void shouldReturnNullIfResourceCannotBeFound()
    {
        when(event.getPointOfFailure()).thenReturn(pof);
        when(lookup.findResourcesForClassName(anyString())).thenReturn(Collections.<IResource> emptyList());

        MarkerPlacement placement = placementStrategy.getPlacement(event);

        assertNull(placement);
    }
}
