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
package org.infinitest.eclipse.markers;

import static com.google.common.collect.Lists.*;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.infinitest.eclipse.workspace.ResourceFinder;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceLookupAdapter implements ResourceLookup
{
    private final ResourceFinder finder;

    @Autowired
    public ResourceLookupAdapter(ResourceFinder finder)
    {
        this.finder = finder;
    }

    public List<IResource> findResourcesForClassName(String className)
    {
        List<IResource> resources = newArrayList();
        for (String sourceFile : possibleFilenamesOf(className))
        {
            IResource resource = finder.findResourceForSourceFile(sourceFile);
            if (resource != null)
            {
                resources.add(resource);
            }
        }
        return resources;
    }

    private Iterable<String> possibleFilenamesOf(String className)
    {
        // Add .groovy, .scala?
        // Maybe this should happen in the resource finder?
        return newArrayList(className.replace(".", "/") + ".java");
    }

    public IWorkspaceRoot workspaceRoot()
    {
        return finder.workspaceRoot();
    }

}
