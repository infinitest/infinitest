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

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static java.util.Collections.*;

import java.util.List;
import java.util.Set;

public class TestCaseEvent
{
    private static final Set<String> IGNORED_ERRORS = newHashSet(VerifyError.class.getName(), Error.class.getName());
    private final List<TestEvent> methodEvents;
    private final Object source;
    private final String testName;
    private final TestResults results;

    public TestCaseEvent(String testName, Object source, TestResults results)
    {
        this.testName = testName;
        this.source = source;
        this.results = results;
        this.methodEvents = newArrayList();
        // DEBT move this to a static factory method?
        for (TestEvent testEvent : results)
        {
            if (!IGNORED_ERRORS.contains(testEvent.getFullErrorClassName()))
            {
                this.methodEvents.add(testEvent);
            }
        }
    }

    public boolean failed()
    {
        return !getFailureEvents().isEmpty();
    }

    public Object getSource()
    {
        return source;
    }

    public String getTestName()
    {
        return testName;
    }

    public List<TestEvent> getFailureEvents()
    {
        return unmodifiableList(methodEvents);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof TestCaseEvent)
        {
            TestCaseEvent other = (TestCaseEvent) obj;
            return testName.equals(other.testName);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return testName.hashCode();
    }

    public Iterable<MethodStats> getRunStats()
    {
        return results.getMethodStats();
    }
}
