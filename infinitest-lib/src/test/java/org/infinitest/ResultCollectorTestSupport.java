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

import static java.util.Arrays.*;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;

public class ResultCollectorTestSupport
{
    protected static final String DEFAULT_TEST_NAME = "testName";
    protected ResultCollector collector;
    protected FailureListenerSupport listener;

    @Before
    public final void processingEventsFromCore()
    {
        collector = new ResultCollector();
        listener = new FailureListenerSupport();
        collector.addChangeListener(listener);
    }

    protected void testRun(TestEvent... events)
    {
        String testName = DEFAULT_TEST_NAME;
        if (events.length != 0)
        {
            testName = events[0].getTestName();
        }
        testRunWith(testName, events);
    }

    protected void testRunWith(String testName, TestEvent... events)
    {
        collector.testCaseComplete(new TestCaseEvent(testName, this, new TestResults(asList(events))));
    }
}
