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

import static org.eclipse.core.resources.IResourceChangeEvent.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.WorkspaceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ClassFileChangeProcessor extends EclipseEventProcessor
{
    private final WorkspaceFacade workspace;

    @Autowired
    ClassFileChangeProcessor(WorkspaceFacade workspace)
    {
        super("Looking for tests");
        this.workspace = workspace;
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        return (event.getType() & (POST_BUILD | POST_CHANGE)) > 0;
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws CoreException
    {
        if (containsClassFileChanges(getDeltas(event)))
        {
            workspace.updateProjects();
        }
    }

    private boolean containsClassFileChanges(IResourceDelta... deltas)
    {
        // DEBT SHould use IResourceDeltaVisitor instead
        for (IResourceDelta delta : deltas)
        {
            if (isClassFile(delta) || containsClassFileChanges(delta.getAffectedChildren()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isClassFile(IResourceDelta delta)
    {
        return delta.getFullPath().toPortableString().endsWith(".class");
    }
}
