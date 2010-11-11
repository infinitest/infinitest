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
package org.infinitest;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.List;

public class ControlledEventQueue implements EventQueue
{
    private final List<Runnable> events = newArrayList();

    public synchronized void push(Runnable runnable)
    {
        events.add(runnable);
    }

    public synchronized void flush()
    {
        // We make a copy because some event handlers fire new events
        ArrayList<Runnable> eventsToFire = newArrayList(events);
        events.clear();
        for (Runnable event : eventsToFire)
        {
            event.run();
        }
        if (!events.isEmpty())
        {
            flush();
        }
    }

    public void pushNamed(NamedRunnable runnable)
    {
        push(runnable);
    }
}
