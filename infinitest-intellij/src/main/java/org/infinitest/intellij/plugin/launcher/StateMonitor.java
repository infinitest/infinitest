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
package org.infinitest.intellij.plugin.launcher;

import static java.lang.System.*;
import static org.infinitest.CoreStatus.*;

import org.infinitest.CoreStatus;
import org.infinitest.StatusChangeListener;

class StateMonitor implements StatusChangeListener
{
    private long cycleStart;
    private boolean statusInFlux;

    public StateMonitor()
    {
        cycleStart = getCurrentTime();
    }

    public long getCycleLengthInMillis()
    {
        return getCurrentTime() - cycleStart;
    }

    public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        if (newStatus.equals(RUNNING))
        {
            statusInFlux = true;
        }
        if (statusInFlux && newStatus.equals(PASSING))
        {
            cycleStart = getCurrentTime();
            statusInFlux = false;
        }
    }

    protected long getCurrentTime()
    {
        return currentTimeMillis();
    }
}
