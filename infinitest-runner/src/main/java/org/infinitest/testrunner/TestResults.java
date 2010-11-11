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
package org.infinitest.testrunner;

import static java.util.Arrays.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TestResults implements Iterable<TestEvent>, Serializable
{
    private static final long serialVersionUID = 1612875588926016329L;

    private final List<TestEvent> eventsCollected;
    private final List<MethodStats> methodStats = new LinkedList<MethodStats>();

    public TestResults(List<TestEvent> eventsCollected)
    {
        this.eventsCollected = eventsCollected;
    }

    public TestResults(TestEvent... failures)
    {
        this(asList(failures));
    }

    public Iterator<TestEvent> iterator()
    {
        return eventsCollected.iterator();
    }

    public Iterable<MethodStats> getMethodStats()
    {
        return methodStats;
    }

    public void addMethodStats(Collection<MethodStats> methodStatistics)
    {
        methodStats.addAll(methodStatistics);
    }
}
