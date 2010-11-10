package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Iterables.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.infinitest.eclipse.markers.ResourceLookupAdapter;
import org.junit.Before;
import org.junit.Test;

public class WhenLookingForResources
{
    private ResourceLookupAdapter adapter;
    private IResource mockResource;
    private ResourceFinder finder;

    @Before
    public void inContext()
    {
        mockResource = mock(IResource.class);
        finder = mock(ResourceFinder.class);
        adapter = new ResourceLookupAdapter(finder);
    }

    @Test
    public void shouldFindAllMatchingResourcesBasedOnClassName()
    {
        when(finder.findResourceForSourceFile("com/foo/Bar.java")).thenReturn(mockResource);

        List<IResource> resources = adapter.findResourcesForClassName("com.foo.Bar");

        assertEquals(mockResource, getOnlyElement(resources));
    }

    @Test
    public void shouldReturnEmptyListIfNoResourcesMatch()
    {
        assertTrue(adapter.findResourcesForClassName("com.foo.Bar").isEmpty());
    }
}
