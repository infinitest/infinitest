package org.infinitest.eclipse.markers;

import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersInTheWorkspace
{
    private TestEvent event;
    private ResourceLookup lookup;
    private IWorkspaceRoot workspaceRoot;

    @Before
    public void inContext()
    {
        event = createEvent("methodName", "message");
        lookup = mock(ResourceLookup.class);
        workspaceRoot = mock(IWorkspaceRoot.class);
    }

    @Test
    public void shouldAlwaysReturnTheWorkspaceResource()
    {
        when(lookup.workspaceRoot()).thenReturn(workspaceRoot);
        MarkerPlacer placer = new MarkerPlacer(lookup);

        MarkerPlacement placement = placer.findPlacement(event);

        assertSame(workspaceRoot, placement.getResource());
    }
}
