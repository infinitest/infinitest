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
package org.infinitest.testrunner.queue;

import static java.lang.Thread.*;
import static java.util.Arrays.*;
import static org.infinitest.EventSupport.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.RunStatistics;
import org.junit.Before;
import org.junit.Test;

public class WhenPrioritizingTests
{
    private RunStatistics stats;
    private TestQueue queue;

    @Before
    public void inContext()
    {
        stats = new RunStatistics();
        queue = new TestQueue(new TestComparator(stats));
    }

    @Test
    public void shouldPreferRecentlyFailedTests() throws Exception
    {
        stats.testCaseComplete(testCaseFailing("test2", "", new Exception()));
        sleep(2);
        stats.testCaseComplete(testCaseFailing("test1", "", new Exception()));

        queue.add("test1");
        queue.add("test2");
        assertEquals("test1", queue.take());
        assertEquals("test2", queue.take());
    }

    @Test
    public void shouldNeverRunTheSameTestTwice()
    {
        queue.addAll(asList("test1", "test2", "test1"));
        assertEquals(2, queue.size());
    }
}
