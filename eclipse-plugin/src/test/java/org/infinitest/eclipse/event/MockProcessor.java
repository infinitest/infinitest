package org.infinitest.eclipse.event;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.event.EclipseEventProcessor;

class MockProcessor extends EclipseEventProcessor
{
    private IResourceChangeEvent eventProcessed;

    public MockProcessor()
    {
        super("Stub");
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        return true;
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws CoreException
    {
        eventProcessed = event;
    }

    public IResourceChangeEvent getEvent()
    {
        return eventProcessed;
    }
}