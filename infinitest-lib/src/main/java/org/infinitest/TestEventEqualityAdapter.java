package org.infinitest;

import java.util.Arrays;

import org.apache.commons.lang.ObjectUtils;
import org.infinitest.testrunner.TestEvent;

public class TestEventEqualityAdapter
{
    private TestEvent event;

    public TestEventEqualityAdapter(TestEvent event)
    {
        this.event = event;
    }

    public TestEvent getEvent()
    {
        return event;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof TestEventEqualityAdapter)
        {
            TestEventEqualityAdapter other = (TestEventEqualityAdapter) obj;
            return ObjectUtils.equals(event, other.event)
                            && ObjectUtils.equals(event.getMessage(), other.event.getMessage())
                            && ObjectUtils.equals(event.getPointOfFailure(), other.event.getPointOfFailure())
                            && Arrays.equals(event.getStackTrace(), other.event.getStackTrace());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(event) ^ ObjectUtils.hashCode(event.getMessage())
                        ^ ObjectUtils.hashCode(event.getPointOfFailure()) ^ Arrays.hashCode(event.getStackTrace());
    }
}