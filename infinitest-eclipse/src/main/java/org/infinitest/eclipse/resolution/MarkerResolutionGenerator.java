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
package org.infinitest.eclipse.resolution;

import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2
{
    public IMarkerResolution[] getResolutions(IMarker marker)
    {
        if (hasResolutions(marker))
        {
            return new IMarkerResolution[] { new ErrorViewerResolution(getTestAndMethodName(marker)) };
        }
        return new IMarkerResolution[0];
    }

    private String getTestAndMethodName(IMarker marker)
    {
        try
        {
            Object attribute = marker.getAttribute(TEST_NAME_ATTRIBUTE);
            if (attribute == null)
            {
                return null;
            }
            String testName = attribute.toString();
            Object methodName = marker.getAttribute(METHOD_NAME_ATTRIBUTE);
            return stripPackageName(testName) + "." + methodName;
        }
        catch (ResourceException e)
        {
            return null;
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean hasResolutions(IMarker marker)
    {
        return getTestAndMethodName(marker) != null;
    }
}
