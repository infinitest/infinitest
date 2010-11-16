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

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.trim.SaveListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class SaveDetector extends EclipseEventProcessor
{
    private final SaveListener saveListener;

    @Autowired
    SaveDetector(SaveListener saveListener)
    {
        super("Looking for changes");
        this.saveListener = saveListener;
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        DeltaVisitor visitor = new DeltaVisitor();
        try
        {
            event.getDelta().accept(visitor, true);
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
        return visitor.savedResourceFound();
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws CoreException
    {
        saveListener.filesSaved();
    }
}
