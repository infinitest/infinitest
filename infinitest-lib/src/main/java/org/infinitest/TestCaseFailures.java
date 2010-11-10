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
