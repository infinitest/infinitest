package org.infinitest.eclipse.workspace;

import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public abstract class FakeResourceFactory
{
    public static IResource stubResource(String path)
    {
        IPath mockPath = mockPath(path);
        return mockResource(mockPath);
    }

    public static IResource mockResource(IPath path)
    {
        IResource resource = mock(IResource.class);
        when(resource.getLocation()).thenReturn(path);
        return resource;
    }

    public static IPath mockPath(String portableString)
    {
        IPath path = mock(IPath.class);
        when(path.toPortableString()).thenReturn(portableString);
        return path;
    }
}
