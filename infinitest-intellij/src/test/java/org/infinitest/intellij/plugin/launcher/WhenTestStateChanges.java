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

import static org.infinitest.CoreStatus.*;
import static org.junit.Assert.*;

import org.infinitest.CoreStatus;
import org.junit.Before;
import org.junit.Test;

public class WhenTestStateChanges
{
    protected long mockTime;
    private StateMonitor monitor;
    private CoreStatus lastStatus = null;

    @Before
    public void inContext()
    {
        mockTime = 0;
    }

    @Test
    public void shouldTrackTimeSinceLastGreenBar()
    {
        monitor = new StateMonitor()
        {
            @Override
            protected long getCurrentTime()
            {
                return mockTime;
            }
        };
        assertEquals(0, monitor.getCycleLengthInMillis());

        mockTime += 1000;
        assertEquals(1000, monitor.getCycleLengthInMillis());

        sendEvents(PASSING);
        assertEquals(1000, monitor.getCycleLengthInMillis());

        mockTime += 1000;
        assertEquals(2000, monitor.getCycleLengthInMillis());

        sendEvents(SCANNING, PASSING);
        assertEquals(2000, monitor.getCycleLengthInMillis());

        mockTime += 1000;
        sendEvents(SCANNING, RUNNING, FAILING);
        assertEquals(3000, monitor.getCycleLengthInMillis());

        mockTime += 1000;
        sendEvents(SCANNING, FAILING);
        assertEquals(4000, monitor.getCycleLengthInMillis());

        sendEvents(SCANNING, RUNNING, PASSING);
        assertEquals(0, monitor.getCycleLengthInMillis());

        mockTime += 1000;
        sendEvents(SCANNING, RUNNING, PASSING);
        assertEquals(0, monitor.getCycleLengthInMillis());

        mockTime += 1000;
        sendEvents(SCANNING, PASSING);
        assertEquals(1000, monitor.getCycleLengthInMillis());
    }

    private void sendEvents(CoreStatus... statuses)
    {
        for (CoreStatus status : statuses)
        {
            monitor.coreStatusChanged(lastStatus, status);
            lastStatus = status;
        }
    }
}
