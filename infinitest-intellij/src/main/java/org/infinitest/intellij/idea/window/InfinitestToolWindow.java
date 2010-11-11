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
package org.infinitest.intellij.idea.window;

import org.infinitest.intellij.idea.facet.FacetListener;
import org.infinitest.intellij.idea.facet.InfinitestFacet;
import org.infinitest.intellij.plugin.InfinitestPlugin;
import org.infinitest.intellij.plugin.InfinitestPluginImpl;
import org.jetbrains.annotations.NotNull;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;

public class InfinitestToolWindow implements ModuleComponent, FacetListener
{
    public static final String TOOL_WINDOW_ID = "Infinitest";

    private InfinitestPlugin plugin;
    private final Project project;
    private final Module module;
    private boolean projectOpened;

    public InfinitestToolWindow(Project project, Module module)
    {
        this.project = project;
        this.module = module;
    }

    public void initComponent()
    {
        // nothing to do here
    }

    public void disposeComponent()
    {
        // nothing to do here
    }

    @NotNull
    public String getComponentName()
    {
        return "org.infinitest.intellij.idea.window.InfinitestToolWindow";
    }

    public void startInfinitestAfterStartup()
    {
        StartupManager.getInstance(project).registerPostStartupActivity(new Runnable()
        {
            public void run()
            {
                plugin.startInfinitest();
            }
        });
    }

    public void facetInitialized()
    {
        if (plugin != null)
        {
            plugin.stopInfinitest();
        }

        InfinitestFacet facet = FacetManager.getInstance(module).getFacetByType(InfinitestFacet.ID);
        plugin = new InfinitestPluginImpl(facet.getConfiguration());

        if (projectOpened)
        {
            plugin.startInfinitest();
        }
        else
        {
            startInfinitestAfterStartup();
        }
    }

    public void facetDisposed()
    {
        if (plugin != null)
        {
            plugin.stopInfinitest();
        }
    }

    public void projectOpened()
    {
        projectOpened = true;
    }

    public void projectClosed()
    {
        if (plugin != null)
        {
            plugin.stopInfinitest();
        }
    }

    public void moduleAdded()
    {
        // nothing to do here
    }
}
