package org.infinitest.eclipse.markers;

import static java.util.Arrays.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.junit.Before;
import org.junit.Test;

public class WhenPlacingMarkersUsingAStackTrace
{
    private static final int SECOND_ELEMENT_LINE_NUMBER = 25;
    private StackTracePlacementStrategy strategy;
    private Throwable error;
    private IResource mockResource;

    @Before
    public void inContext()
    {
        ResourceLookup lookup = mock(ResourceLookup.class);
        mockResource = mock(IResource.class);
        when(lookup.findResourcesForClassName("com.myco.Foo")).thenReturn(asList(mockResource));
        strategy = new StackTracePlacementStrategy(lookup);
        error = mock(Throwable.class);
    }

    @Test
    public void shouldFindTheFirstClassInTheWorkspace()
    {
        expectStackTraceWithClass("com.myco.Foo");

        MarkerPlacement placement = strategy.getPlacement(eventWithError(error));

        assertEquals(SECOND_ELEMENT_LINE_NUMBER, placement.getLineNumber());
        assertSame(mockResource, placement.getResource());
    }

    @Test
    public void shouldReturnNullIfNoClassesAreInTheWorkspace()
    {
        expectStackTraceWithClass("com.myco.Baz");

        assertNull(strategy.getPlacement(eventWithError(error)));
    }

    private void expectStackTraceWithClass(String secondElementClass)
    {
        StackTraceElement[] stackTrace = new StackTraceElement[] {
                        new StackTraceElement("com.myco.Bar", "method", "file", 10),
                        new StackTraceElement(secondElementClass, "method", "file", SECOND_ELEMENT_LINE_NUMBER) };
        when(error.getStackTrace()).thenReturn(stackTrace);
    }
}
