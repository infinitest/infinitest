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

import static org.infinitest.util.InfinitestGlobalSettings.*;

import java.util.Collection;

import org.infinitest.DisabledTestListener;
import org.infinitest.eclipse.markers.MarkerRegistry;
import org.infinitest.eclipse.markers.SlowMarkerRegistry;
import org.infinitest.eclipse.markers.SlowTestMarkerInfo;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.infinitest.testrunner.MethodStats;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResultsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlowTestObserver implements TestResultsListener, DisabledTestListener
{
    private final MarkerRegistry slowMarkerRegistry;
    private final ResourceLookup lookup;

    @Autowired
    public SlowTestObserver(SlowMarkerRegistry slowMarkerRegistry, ResourceLookup lookup)
    {
        this.slowMarkerRegistry = slowMarkerRegistry;
        this.lookup = lookup;
    }

    private void runStatsUpdated(String testName, MethodStats newStats)
    {
        // DEBT This class wants to share a lot of code with ResultCollector
        // The whole process of detecting passing/failing/ignored/disabled tests is remarkably
        // similar to the process of detecting slow/fast/ignored/disabled tests.
        SlowTestMarkerInfo marker = new SlowTestMarkerInfo(testName, newStats, lookup);
        if (newStats.duration() > getSlowTestTimeLimit())
        {
            slowMarkerRegistry.addMarker(marker);
        }
        else
        {
            slowMarkerRegistry.removeMarker(marker);
        }
    }

    public void testCaseComplete(TestCaseEvent event)
    {
        for (MethodStats methodStat : event.getRunStats())
        {
            runStatsUpdated(event.getTestName(), methodStat);
        }
    }

    public void testCaseStarting(TestEvent event)
    {
    }

    public void testsDisabled(Collection<String> testName)
    {
        for (String eachTest : testName)
        {
            slowMarkerRegistry.removeMarkers(eachTest);
        }
    }

    public void clearMarkers()
    {
        slowMarkerRegistry.clear();
    }
}
