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