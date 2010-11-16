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

import static com.google.common.collect.Lists.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;

import java.io.Serializable;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.infinitest.eclipse.InfinitestPlugin;
import org.infinitest.eclipse.workspace.ResourceLookup;

public class ErrorViewerResolution implements IMarkerResolution2
{
    private final String name;
    private final StackTraceFilter stackTraceFilter;

    public ErrorViewerResolution(String testAndMethodName)
    {
        name = testAndMethodName;
        stackTraceFilter = new StackTraceFilter();
    }

    public String getDescription()
    {
        return "Shows the details for this test failure";
    }

    public Image getImage()
    {
        return null;
    }

    public String getLabel()
    {
        return name + " failing (see details)";
    }

    public void run(IMarker marker)
    {
        try
        {
            Serializable unpickledStack = unpickle(marker.getAttribute(PICKLED_STACK_TRACE_ATTRIBUTE).toString());
            List<StackTraceElement> stackTrace = newArrayList((StackTraceElement[]) unpickledStack);
            createStackViewWith(stackTrace, marker.getAttribute(MESSAGE).toString());
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected void createStackViewWith(List<StackTraceElement> stackTrace, String message)
    {
        List<StackTraceElement> filteredStackTrace = stackTraceFilter.filterStack(stackTrace);
        ResourceLookup resourceLookup = InfinitestPlugin.getInstance().getBean(ResourceLookup.class);
        new FailureViewer(getMainShell(), message, filteredStackTrace, resourceLookup).show();
    }

    private Shell getMainShell()
    {
        // RISK Untested
        for (Shell shell : Display.getDefault().getShells())
        {
            if (isPrimary(shell))
            {
                return shell;
            }
        }
        // This is problematic because the active shell may be the Problems View QuickFix dialog,
        // rather than the main eclipse shell
        return Display.getDefault().getActiveShell();
    }

    private boolean isPrimary(Shell shell)
    {
        // This is my best guess at how to identify the primary shell
        return shell.getParent() == null;
    }
}
