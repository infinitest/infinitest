/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.eclipse;

import static org.mockito.Mockito.*;

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
}
