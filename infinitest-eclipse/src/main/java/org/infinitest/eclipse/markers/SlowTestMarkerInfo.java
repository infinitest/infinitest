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

import static com.google.common.collect.Maps.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.MethodStats;

public class SlowTestMarkerInfo extends AbstractMarkerInfo
{
    private final String testName;
    private final MethodStats methodStats;
    private final ResourceLookup lookup;

    public SlowTestMarkerInfo(String testName, MethodStats methodStats, ResourceLookup lookup)
    {
        this.testName = testName;
        this.methodStats = methodStats;
        this.lookup = lookup;
    }

    @Override
    public IResource associatedResource() throws CoreException
    {
        List<IResource> resources = lookup.findResourcesForClassName(testName);
        if (resources.isEmpty())
        {
            return null;
        }
        return resources.get(0);
    }

    public Map<String, Object> attributes()
    {
        Map<String, Object> markerAttributes = newLinkedHashMap();
        markerAttributes.put(SEVERITY, SEVERITY_WARNING);
        markerAttributes.put(MESSAGE, buildMessage());

        return markerAttributes;
    }

    private String buildMessage()
    {
        return stripPackageName(testName) + "." + methodStats.methodName + " ran in " + methodStats.duration() + "ms";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof SlowTestMarkerInfo)
        {
            SlowTestMarkerInfo other = (SlowTestMarkerInfo) obj;
            return testName.equals(other.testName) && methodStats.methodName.equals(other.methodStats.methodName);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return testName.hashCode() ^ methodStats.methodName.hashCode();
    }

    public String getTestName()
    {
        return testName;
    }

}
