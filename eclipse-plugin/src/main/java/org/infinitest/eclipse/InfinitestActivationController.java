package org.infinitest.eclipse;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.infinitest.EventQueue;
import org.infinitest.NamedRunnable;
import org.infinitest.eclipse.trim.VisualStatus;
import org.infinitest.eclipse.trim.VisualStatusRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InfinitestActivationController implements PluginActivationController
{
    private boolean pluginEnabled;
    private VisualStatusRegistry visualStatusRegistry;
    private EventQueue eventQueue;
    private NamedRunnable markerClearingRunnable;
    private IResourceChangeListener updateNotifier;
    private IWorkspace workspace;

    @Autowired
    public InfinitestActivationController()
    {
    }

    @Autowired
    public void setMarkerClearingRunnable(NamedRunnable markerClearingRunnable)
    {
        this.markerClearingRunnable = markerClearingRunnable;
    }

   
    @Autowired
    public void setVisualStatusRegistry(VisualStatusRegistry visualStatusRegistry)
    {
        this.visualStatusRegistry = visualStatusRegistry;
    }

    @Autowired
    public void setEventQueue(EventQueue eventQueue)
    {
        this.eventQueue = eventQueue;
    }

    @Autowired
    public void setWorkspace(IWorkspace workspace)
    {
        this.workspace = workspace;
    }

    @Autowired
    public void setUpdateNotifier(IResourceChangeListener updateNotifier)
    {
        this.updateNotifier = updateNotifier;
    }

    public void enable()
    {
        if (!pluginEnabled)
            attachListener();
    }

    public void disable()
    {
        workspace.removeResourceChangeListener(updateNotifier);
        pluginEnabled = false;

        eventQueue.pushNamed(markerClearingRunnable);
    }

    private void attachListener()
    {
        pluginEnabled = true;
        workspace.addResourceChangeListener(updateNotifier);
    }

    public void attachVisualStatus(VisualStatus status)
    {
        visualStatusRegistry.updateVisualStatus(status);
    }
}
