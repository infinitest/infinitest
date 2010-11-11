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

import static org.easymock.EasyMock.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.infinitest.testrunner.TestEvent;

final class FakeProblemMarkerInfo extends ProblemMarkerInfo
{
    public FakeProblemMarkerInfo(TestEvent event)
    {
        super(event, new FakeResourceFinder());
    }

    @Override
    public IResource associatedResource() throws CoreException
    {
        IResource mockResource = createNiceMock(IResource.class);
        IMarker marker = createNiceMock(IMarker.class);
        expect(mockResource.createMarker(isA(String.class))).andReturn(marker);

        replay(mockResource);
        return mockResource;
    }
}