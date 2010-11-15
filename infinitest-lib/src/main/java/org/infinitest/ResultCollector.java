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
import static com.google.common.collect.Maps.*;
import static java.util.Collections.*;
import static org.infinitest.CoreStatus.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.infinitest.testrunner.PointOfFailure;
import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Listens to events fired by the infinitest core to track the state of individual test cases.
 * 
 * @author bjrady
 */
public class ResultCollector extends TestResultsAdapter implements DisabledTestListener, TestQueueListener
{
    private CoreStatus status;
    private final Map<String, TestCaseEvent> resultMap;
    private final List<FailureListListener> changeListeners;
    private final List<StatusChangeListener> statusChangeListeners;
    private final ListMultimap<PointOfFailure, TestEvent> failuresByPointOfFailure;
    private final QueueAggregator queueAggregator;

    public ResultCollector()
    {
        resultMap = newHashMap();
        changeListeners = newArrayList();
        statusChangeListeners = newArrayList();
        failuresByPointOfFailure = ArrayListMultimap.create();
        status = SCANNING;
        queueAggregator = new QueueAggregator();
        queueAggregator.addListener(this);
    }

    public ResultCollector(InfinitestCore core)
    {
        this();
        attachCore(core);
    }

    public void attachCore(InfinitestCore core)
    {
        core.addDisabledTestListener(this);
        core.addTestResultsListener(this);
        queueAggregator.attach(core);
    }

    public void detachCore(InfinitestCore core)
    {
        queueAggregator.detach(core);
        core.removeTestResultsListener(this);
        core.removeDisabledTestListener(this);
        List<String> tests = findFailingTestsForCore(core);
        for (String string : tests)
        {
            resultMap.remove(string);
        }
    }

    private List<String> findFailingTestsForCore(InfinitestCore core)
    {
        List<String> tests = newArrayList();
        for (TestCaseEvent eachEvent : resultMap.values())
        {
            if (core.isEventSourceFor(eachEvent))
            {
                tests.add(eachEvent.getTestName());
            }
        }
        return tests;
    }

    public void addStatusChangeListener(StatusChangeListener listener)
    {
        statusChangeListeners.add(listener);
    }

    public void removeStatusChangeListener(StatusChangeListener listener)
    {
        statusChangeListeners.remove(listener);
    }

    @Override
    public void testCaseComplete(TestCaseEvent event)
    {
        TestCaseFailures failureSet = getCurrentFailuresForTestCase(event);
        for (TestEvent each : event.getFailureEvents())
        {
            failuresByPointOfFailure.put(each.getPointOfFailure(), each);
            failureSet.addNewFailure(each);
        }
        resultMap.put(event.getTestName(), event);
        fireCachedFailureEvents(failureSet);
    }

    private TestCaseFailures getCurrentFailuresForTestCase(TestCaseEvent event)
    {
        TestCaseEvent oldEvent = resultMap.get(event.getTestName());
        if (oldEvent != null)
        {
            return new TestCaseFailures(oldEvent.getFailureEvents());
        }
        return new TestCaseFailures(noEvents());
    }

    private void fireCachedFailureEvents(TestCaseFailures testCaseFailures)
    {
        fireChangeEvent(testCaseFailures.newFailures(), testCaseFailures.removedFailures());
        fireUpdateEvent(testCaseFailures.updatedFailures());
    }

    public List<PointOfFailure> getPointsOfFailure()
    {
        List<PointOfFailure> resultList = newArrayList();
        for (TestEvent failure : getFailures())
        {
            PointOfFailure pointOfFailure = failure.getPointOfFailure();
            if (!resultList.contains(pointOfFailure))
            {
                resultList.add(pointOfFailure);
            }
        }
        return resultList;
    }

    public PointOfFailure getPointOfFailure(int i)
    {
        return getPointsOfFailure().get(i);
    }

    public boolean isPointOfFailure(Object parent)
    {
        return getPointsOfFailure().contains(parent);
    }

    public List<TestEvent> getTestsFor(PointOfFailure pointOfFailure)
    {
        List<TestEvent> matchedEvents = new ArrayList<TestEvent>();
        for (TestEvent event : getFailures())
        {
            if (event.getPointOfFailure().equals(pointOfFailure))
            {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;
    }

    public int getPointOfFailureCount()
    {
        return getPointsOfFailure().size();
    }

    public int getPointOfFailureIndex(PointOfFailure pointOfFailure)
    {
        return getPointsOfFailure().indexOf(pointOfFailure);
    }

    public void addChangeListener(FailureListListener listener)
    {
        changeListeners.add(listener);
    }

    public boolean hasFailures()
    {
        return !getFailures().isEmpty();
    }

    public List<TestEvent> getFailures()
    {
        List<TestEvent> failures = newArrayList();
        for (TestCaseEvent each : resultMap.values())
        {
            failures.addAll(each.getFailureEvents());
        }
        return failures;
    }

    public void clear()
    {
        resultMap.clear();
        failuresByPointOfFailure.clear();
    }

    public List<TestEvent> getFailuresForPointOfFailure(PointOfFailure pointOfFailure)
    {
        return failuresByPointOfFailure.get(pointOfFailure);
    }

    public CoreStatus getStatus()
    {
        return status;
    }

    public void testsDisabled(Collection<String> testNames)
    {
        for (String eachTest : testNames)
        {
            TestCaseEvent event = resultMap.remove(eachTest);
            if (event != null)
            {
                fireChangeEvent(noEvents(), event.getFailureEvents());
            }
        }
    }

    public void addTestQueueListener(TestQueueListener testQueueAdapter)
    {
        queueAggregator.addListener(testQueueAdapter);
    }

    public void removeTestQueueListener(TestQueueListener listener)
    {
        queueAggregator.removeTestQueueListener(listener);
    }

    public void testRunComplete()
    {
        if (hasFailures())
        {
            setStatus(FAILING);
        }
        else
        {
            setStatus(PASSING);
        }
        log("Update complete. Status " + getStatus());
    }

    public void testQueueUpdated(TestQueueEvent event)
    {
        if (!event.getTestQueue().isEmpty())
        {
            setStatus(RUNNING);
        }
    }

    public void reloading()
    {
        List<TestEvent> failuresRemoved = newArrayList(getFailures());
        clear();
        setStatus(SCANNING);
        fireChangeEvent(noEvents(), failuresRemoved);
    }

    private List<TestEvent> noEvents()
    {
        return emptyList();
    }

    private void setStatus(CoreStatus newStatus)
    {
        CoreStatus oldStatus = status;
        status = newStatus;
        fireStatusChanged(oldStatus, newStatus);
    }

    private void fireStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        for (StatusChangeListener each : statusChangeListeners)
        {
            each.coreStatusChanged(oldStatus, newStatus);
        }
    }

    private void fireChangeEvent(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved)
    {
        for (FailureListListener each : changeListeners)
        {
            each.failureListChanged(failuresAdded, failuresRemoved);
        }
    }

    private void fireUpdateEvent(Collection<TestEvent> updatedFailures)
    {
        for (FailureListListener each : changeListeners)
        {
            each.failuresUpdated(updatedFailures);
        }
    }
}
