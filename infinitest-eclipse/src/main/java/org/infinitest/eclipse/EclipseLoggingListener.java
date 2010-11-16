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

import static java.util.logging.Level.*;
import static org.infinitest.eclipse.InfinitestPlugin.*;

import java.util.logging.Level;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.infinitest.util.LoggingListener;

public class EclipseLoggingListener implements LoggingListener
{
    public void logError(String message, Throwable throwable)
    {
        Status status = new Status(IStatus.ERROR, PLUGIN_ID, message, throwable);
        InfinitestPlugin plugin = InfinitestPlugin.getInstance();
        if (plugin != null)
        {
            plugin.getLog().log(status);
        }
    }

    public void logMessage(Level level, String message)
    {
        Status status = new Status(levelToStatus(level), PLUGIN_ID, message);
        InfinitestPlugin plugin = InfinitestPlugin.getInstance();
        if (plugin != null)
        {
            plugin.getLog().log(status);
        }
    }

    private int levelToStatus(Level level)
    {
        if (level.equals(SEVERE))
        {
            return IStatus.ERROR;
        }
        if (level.equals(WARNING))
        {
            return IStatus.WARNING;
        }
        return IStatus.INFO;
    }
}
