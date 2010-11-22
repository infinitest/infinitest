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
package org.infinitest.util;

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
            assertFalse(equal.equals(other));
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
}
