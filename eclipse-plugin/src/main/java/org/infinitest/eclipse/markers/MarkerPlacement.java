package org.infinitest.eclipse.markers;

import org.eclipse.core.resources.IResource;

public class MarkerPlacement
{
    private final IResource resource;
    private final int lineNumber;

    public MarkerPlacement(IResource resource, int lineNumber)
    {
        this.resource = resource;
        this.lineNumber = lineNumber;
    }

    public IResource getResource()
    {
        return resource;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }
}
