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

import java.util.Collection;

import org.infinitest.FailureListListener;
import org.infinitest.eclipse.markers.MarkerRegistry;
import org.infinitest.eclipse.markers.ProblemMarkerInfo;
import org.infinitest.eclipse.markers.ProblemMarkerRegistry;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.TestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FailureMediator implements FailureListListener
{
    private final MarkerRegistry registry;
    private final ResourceLookup resourceLookup;

    @Autowired
    public FailureMediator(ProblemMarkerRegistry registry, ResourceLookup resourceLookup)
    {
        this.registry = registry;
        this.resourceLookup = resourceLookup;
    }

    public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
    {
        // Note that logging this may kill performance when you have a lot of errors
        for (TestEvent testEvent : failuresAdded)
        {
            registry.addMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
        }

        for (TestEvent testEvent : failuresRemoved)
        {
            registry.removeMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
        }
    }

    public void failuresUpdated(Collection<TestEvent> updatedFailures)
    {
        for (TestEvent testEvent : updatedFailures)
        {
            registry.updateMarker(new ProblemMarkerInfo(testEvent, resourceLookup));
        }
    }
}
