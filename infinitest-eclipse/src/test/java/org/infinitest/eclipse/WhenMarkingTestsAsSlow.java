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

import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.junit.Assert.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.markers.SlowTestMarkerInfo;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.MethodStats;
import org.infinitest.util.EqualityTestSupport;
import org.junit.Before;
import org.junit.Test;

public class WhenMarkingTestsAsSlow extends EqualityTestSupport
{
    private static final String TEST_NAME = "com.foo.TestName";
    private SlowTestMarkerInfo marker;
    private ResourceLookup resourceLookup;

    @Before
    public void inContext()
    {
        resourceLookup = createMock(ResourceLookup.class);
        MethodStats stats = new MethodStats("shouldRunSlowly");
        stats.stopTime = 5000;
        marker = new SlowTestMarkerInfo(TEST_NAME, stats, resourceLookup);
    }

    @Test
    public void shouldPlaceMarkerInSlowTest() throws CoreException
    {
        IResource resource = createMock(IResource.class);
        expect(resourceLookup.findResourcesForClassName(TEST_NAME)).andReturn(asList(resource));
        replay(resourceLookup);
        assertSame(resource, marker.associatedResource());
    }

    @Test
    public void shouldUseWarningSeverity()
    {
        assertEquals(SEVERITY_WARNING, marker.attributes().get(SEVERITY));
    }

    @Test
    public void shouldIndicateTestAndMethodName()
    {
        assertEquals("TestName.shouldRunSlowly ran in 5000ms", marker.attributes().get(MESSAGE));
    }

    @Override
    protected Object createEqualInstance()
    {
        return new SlowTestMarkerInfo(TEST_NAME, new MethodStats("shouldRunSlowly"), resourceLookup);
    }

    @Override
    protected Object createUnequalInstance()
    {
        return new SlowTestMarkerInfo(TEST_NAME, new MethodStats("shouldRunQuickly"), resourceLookup);
    }
}
