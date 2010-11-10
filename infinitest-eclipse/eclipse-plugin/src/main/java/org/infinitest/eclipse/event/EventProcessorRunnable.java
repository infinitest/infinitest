package org.infinitest.eclipse.event;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.NamedRunnable;

class EventProcessorRunnable extends NamedRunnable
{
    private final EclipseEventProcessor eclipseEventProcessor;
    private final IResourceChangeEvent event;

    EventProcessorRunnable(EclipseEventProcessor eclipseEventProcessor, IResourceChangeEvent event)
    {
        super(eclipseEventProcessor.getJobName());
        this.eclipseEventProcessor = eclipseEventProcessor;
        this.event = event;
    }

    public void run()
    {
        try
        {
            eclipseEventProcessor.processEvent(event);
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }
}