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
import static com.google.common.collect.Sets.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.infinitest.testrunner.TestEvent;

public class TestCaseFailures
{
    private final Set<TestEvent> existingFailures;
    private final Set<TestEvent> newFailures;

    public TestCaseFailures(Collection<TestEvent> existingFailures)
    {
        this.existingFailures = newHashSet(existingFailures);
        this.newFailures = newHashSet();
    }

    public void addNewFailure(TestEvent newFailure)
    {
        newFailures.add(newFailure);
    }

    public Collection<TestEvent> updatedFailures()
    {
        Set<TestEvent> eventsFromAFailingTest = intersection(newFailures, existingFailures);
        List<TestEvent> eventsThatAreFailingForDifferentReason = listOf(difference(setOf(eventsFromAFailingTest),
                        setOf(existingFailures)));
        return eventsThatAreFailingForDifferentReason;
    }

    public static List<TestEvent> listOf(Set<TestEventEqualityAdapter> set)
    {
        ArrayList<TestEvent> list = newArrayList();
        for (TestEventEqualityAdapter each : set)
        {
            list.add(each.getEvent());
        }
        return list;
    }

    public static LinkedHashSet<TestEventEqualityAdapter> setOf(Collection<TestEvent> failures)
    {
        LinkedHashSet<TestEventEqualityAdapter> set = newLinkedHashSet();
        for (TestEvent testEvent : failures)
        {
            set.add(new TestEventEqualityAdapter(testEvent));
        }
        return set;
    }

    public Collection<TestEvent> newFailures()
    {
        return difference(newFailures, existingFailures);
    }

    public Collection<TestEvent> removedFailures()
    {
        return difference(existingFailures, newFailures);
    }
}
