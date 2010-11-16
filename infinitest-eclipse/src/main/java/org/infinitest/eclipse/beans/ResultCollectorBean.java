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
package org.infinitest.eclipse.beans;

import org.infinitest.FailureListListener;
import org.infinitest.ResultCollector;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestQueueListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultCollectorBean extends ResultCollector
{
    // Too simple to break, no?

    @Autowired
    public void addChangeListeners(FailureListListener... listeners)
    {
        for (FailureListListener each : listeners)
        {
            addChangeListener(each);
        }
    }

    @Autowired
    public void addTestQueueListeners(TestQueueListener... listeners)
    {
        for (TestQueueListener each : listeners)
        {
            addTestQueueListener(each);
        }
    }

    @Autowired
    public void addStatusChangeListeners(StatusChangeListener... listeners)
    {
        for (StatusChangeListener each : listeners)
        {
            addStatusChangeListener(each);
        }
    }
}
