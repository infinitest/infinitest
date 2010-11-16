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

import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.TestEvent;
import org.infinitest.util.EqualityTestSupport;
import org.junit.Before;
import org.junit.Test;

public class WhenStrictlyComparingTestEvents extends EqualityTestSupport
{
    private Throwable throwable;
    private TestEventEqualityAdapter event1;
    private TestEventEqualityAdapter event2;

    @Before
    public void inContext()
    {
        throwable = new Throwable();
        throwable.fillInStackTrace();
        event1 = createEqualInstance();
        event2 = createUnequalInstance();
    }

    @Test
    public void shouldIncludeLineNumberInEventComparison()
    {
        throwable = new Throwable();
        throwable.fillInStackTrace();
        event2 = adapterFor(methodFailed("message1", "testName", "methodName", throwable));
        assertThat(event1, not(equalTo(event2)));
    }

    @Test
    public void shouldIncludeStackTraceOfFailureInEventComparison()
    {
        throwable = throwableWithStack(new StackTraceElement("declaringClass", "methodName", "fileName", 1),
                        new StackTraceElement("declaringClass", "anotherMethod", "fileName", 1));

        Throwable anotherThrowable = throwableWithStack(new StackTraceElement("declaringClass", "methodName",
                        "fileName", 1), new StackTraceElement("declaringClass", "anotherMethod", "fileName", 2));

        assertEquals(throwable.getClass(), anotherThrowable.getClass());

        event1 = adapterFor(methodFailed("message1", "testName", "methodName", throwable));
        event2 = adapterFor(methodFailed("message1", "testName", "methodName", anotherThrowable));
        assertThat(event1, not(equalTo(event2)));
    }

    private TestEventEqualityAdapter adapterFor(TestEvent event)
    {
        return new TestEventEqualityAdapter(event);
    }

    @Override
    protected TestEventEqualityAdapter createEqualInstance()
    {
        return adapterFor(methodFailed("message1", "testName", "methodName", throwable));
    }

    @Override
    protected TestEventEqualityAdapter createUnequalInstance()
    {
        return adapterFor(methodFailed("message2", "testName", "methodName", throwable));
    }
}
