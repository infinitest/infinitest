package org.infinitest;

import static com.google.common.collect.Lists.*;

import java.util.Collection;
import java.util.List;

import org.infinitest.testrunner.TestEvent;

class FailureListenerSupport implements FailureListListener
{
    public final List<TestEvent> added;
    public final List<TestEvent> removed;
    public final List<TestEvent> failures;
    public final List<TestEvent> changed;

    FailureListenerSupport()
    {
        this.added = newArrayList();
        this.removed = newArrayList();
        this.failures = newArrayList();
        this.changed = newArrayList();
    }

    public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
    {
        added.addAll(failuresAdded);
        removed.addAll(failuresRemoved);
        failures.addAll(failuresAdded);
        failures.removeAll(failuresRemoved);
    }

    public void failuresUpdated(Collection<TestEvent> updatedFailures)
    {
        changed.addAll(updatedFailures);
    }

    public void clear()
    {
        added.clear();
        removed.clear();
        failures.clear();
        changed.clear();
    }
}