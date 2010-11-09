package org.infinitest.eclipse.markers;

import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public abstract class AbstractMarkerInfo implements MarkerInfo
{
    public IMarker createMarker(String markerId)
    {
        IMarker marker;
        try
        {
            IResource sourceFileResource = associatedResource();
            marker = sourceFileResource.createMarker(markerId);
            marker.setAttributes(attributes());
            return marker;
        }
        catch (CoreException e)
        {
            log("Error creating marker " + this, e);
            throw new RuntimeException();
        }
    }

    protected abstract IResource associatedResource() throws CoreException;
}
