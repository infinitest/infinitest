package org.infinitest.eclipse.event;

import static org.eclipse.core.resources.IResourceDelta.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

class DeltaVisitor implements IResourceDeltaVisitor
{
    private static final boolean KEEP_SEARCHING = true;
    private static final boolean STOP_SEARCHING = false;
    private boolean savedResourceFound;

    public boolean visit(IResourceDelta delta) throws CoreException
    {
        IResource resource = delta.getResource();
        if (!resource.isDerived())
        {
            if (resource.getType() != IResource.FILE)
            {
                return KEEP_SEARCHING;
            }
            savedResourceFound = notOnlyMarkersChanged(delta);
        }
        return STOP_SEARCHING;
    }

    private boolean notOnlyMarkersChanged(IResourceDelta delta)
    {
        return delta.getFlags() != MARKERS;
    }

    public boolean savedResourceFound()
    {
        return savedResourceFound;
    }
}