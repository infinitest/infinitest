package org.infinitest.eclipse.event;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

public abstract class EclipseEventProcessor
{
    private final String jobName;

    public EclipseEventProcessor(String jobName)
    {
        this.jobName = jobName;
    }

    public String getJobName()
    {
        return jobName;
    }

    public abstract boolean canProcessEvent(IResourceChangeEvent event);

    public abstract void processEvent(IResourceChangeEvent event) throws CoreException;

    protected IResourceDelta[] getDeltas(IResourceChangeEvent event)
    {
        return event.getDelta().getAffectedChildren();
    }
}
