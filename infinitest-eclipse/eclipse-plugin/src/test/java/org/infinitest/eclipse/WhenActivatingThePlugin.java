package org.infinitest.eclipse;

import static org.infinitest.eclipse.InfinitestPlugin.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.infinitest.EventQueue;
import org.infinitest.NamedRunnable;
import org.infinitest.eclipse.trim.VisualStatusRegistry;
import org.junit.Before;
import org.junit.Test;

public class WhenActivatingThePlugin
{
    private IWorkspace workspace;
    private InfinitestActivationController controller;
    private IResourceChangeListener coreUpdateNotifier;
    private VisualStatusRegistry visualStatusRegistry;
    private EventQueue eventQueue;
    private NamedRunnable markerClearingRunnable;

    @Before
    public final void inContext()
    {
        workspace = mock(IWorkspace.class);
        coreUpdateNotifier = mock(IResourceChangeListener.class);
        visualStatusRegistry = mock(VisualStatusRegistry.class);
        eventQueue = mock(EventQueue.class);
        markerClearingRunnable = mock(NamedRunnable.class);

        controller = new InfinitestActivationController();
        controller.setVisualStatusRegistry(visualStatusRegistry);
        controller.setWorkspace(workspace);
        controller.setUpdateNotifier(coreUpdateNotifier);
        controller.setEventQueue(eventQueue);
        controller.setMarkerClearingRunnable(markerClearingRunnable);
    }

    @Test
    public void shouldNotAddTwoListenerIfPluginIsEnabledTwice()
    {
        controller.enable();
        controller.enable();

        verify(workspace).addResourceChangeListener(coreUpdateNotifier);
    }

    @Test
    public void shouldRemoveListenersWhenDisabled()
    {
        controller.disable();
        verify(workspace).removeResourceChangeListener(coreUpdateNotifier);
    }

    @Test
    public void shouldClearMarkersOnEventQueueWhenDisabled()
    {
        controller.disable();
        verify(eventQueue).pushNamed(markerClearingRunnable);
    }

    @Test
    public void shouldDefaultToTheFarFutureIfNoPluginReleaseDateCanBeFound()
    {
        assertEquals(new Date(Long.MAX_VALUE), parseReleaseDate("not a date"));
    }
}
