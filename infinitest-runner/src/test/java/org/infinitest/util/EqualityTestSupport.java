package org.infinitest.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public abstract class EqualityTestSupport
{
    protected abstract Object createEqualInstance();

    protected abstract Object createUnequalInstance();

    protected List<Object> createUnequalInstances()
    {
        List<Object> list = new ArrayList<Object>();
        list.add(createUnequalInstance());
        return list;
    }

    @Test
    public void identicalObjectsShouldBeEqual()
    {
        Object reference = createEqualInstance();
        assertEquals(reference, reference);
    }

    @Test
    public void unequalInstancesAreUnique()
    {
        List<Object> unequalInstances = createUnequalInstances();
        HashSet<Object> set = new HashSet<Object>(unequalInstances);
        assertEquals(set.size(), unequalInstances.size());
    }

    @Test
    public void differentObjectsShouldBeUnequal()
    {
        Object equal = createEqualInstance();
        for (Object other : createUnequalInstances())
        {
            assertThat(equal, not(equalTo(other)));
        }
    }

    @Test
    public void equalObjectsShouldHaveSameHashcode()
    {
        assertEquals(createEqualInstance().hashCode(), createEqualInstance().hashCode());
    }

    @Test
    public void shouldDifferentTypesShouldNotBeEqual()
    {
        assertFalse(createEqualInstance().equals(new Object()));
    }

    /**
     * For performance reasons
     */
    @Test
    public void differentObjectsShouldHaveDifferentHashcodes()
    {
        Object equal = createEqualInstance();
        for (Object other : createUnequalInstances())
        {
            assertThat(equal.hashCode(), not(equalTo(other.hashCode())));
        }
    }
}
