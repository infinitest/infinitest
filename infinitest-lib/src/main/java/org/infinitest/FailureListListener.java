package org.infinitest;

import java.util.Collection;

import org.infinitest.testrunner.TestEvent;

public interface FailureListListener
{
    void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved);

    void failuresUpdated(Collection<TestEvent> updatedFailures);
}
