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
        {
            attachListener();
        }
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
