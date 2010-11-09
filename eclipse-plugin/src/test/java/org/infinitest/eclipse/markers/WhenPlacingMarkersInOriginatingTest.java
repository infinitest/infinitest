package org.infinitest.eclipse.markers;

import static com.google.common.collect.Lists.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersInOriginatingTest
{
    private TestNamePlacementStrategy strategy;
    private TestEvent event;
    private IResource testResource;
    private ResourceLookup lookup;

    @Before
    public void inContext()
    {
        testResource = mock(IResource.class);
        lookup = mock(ResourceLookup.class);
        strategy = new TestNamePlacementStrategy(lookup);
        event = eventWithError(new AssertionError());
    }

    @Test
    public void shouldUseTestNameInEvent()
    {
        when(lookup.findResourcesForClassName(TEST_NAME)).thenReturn(newArrayList(testResource));

        MarkerPlacement placement = strategy.getPlacement(event);

        assertSame(testResource, placement.getResource());
        assertEquals("Line zero indicates we don't know where to place the marker", 0, placement.getLineNumber());
    }

    @Test
    public void shouldReturnNullIfTestCannotBeFoundInWorkspace()
    {
        assertNull(strategy.getPlacement(event));
    }
}
