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
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.util.List;

import org.infinitest.util.EqualityTestSupport;
import org.junit.Before;
import org.junit.Test;

public class TestCaseEventTest extends EqualityTestSupport
{
    private TestCaseEvent event;
    private List<TestEvent> methodEvents;
    private Object source;

    @Before
    public void inContext()
    {
        methodEvents = newArrayList();
        source = "";
        event = new TestCaseEvent("testName", source, new TestResults(methodEvents));
    }

    @Test
    public void shouldIgnoreCompilerErrors()
    {
        methodEvents.add(methodFailed("TestClass", "", new VerifyError()));
        methodEvents.add(methodFailed("TestClass", "", new Error()));
        event = new TestCaseEvent("testName", source, new TestResults(methodEvents));
        assertFalse(event.failed());
    }

    @Test
    public void shouldContainMethodEvents()
    {
        assertFalse(event.failed());
    }

    @Test
    public void shouldIndicateSourceOfEvent()
    {
        assertEquals(source, event.getSource());
    }

    @Override
    protected TestCaseEvent createEqualInstance()
    {
        return new TestCaseEvent("testName", source, new TestResults());
    }

    @Override
    protected Object createUnequalInstance()
    {
        return new TestCaseEvent("testName2", source, new TestResults());
    }
}
