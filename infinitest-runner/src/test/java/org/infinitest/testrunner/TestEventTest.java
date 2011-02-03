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

import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import jdave.test.JDaveUtils;

import org.infinitest.util.EqualityTestSupport;
import org.junit.Before;
import org.junit.Test;

public class TestEventTest extends EqualityTestSupport
{
    private TestEvent event;
    private Throwable error;

    @Before
    public void inContext()
    {
        try
        {
            throw new UnserializableException("Exception Message");
        }
        catch (UnserializableException e)
        {
            error = e;
        }
        event = eventWithError(error);
    }

    @SuppressWarnings("serial")
    public class UnserializableException extends RuntimeException
    {
        public UnserializableException(String string)
        {
            super(string);
        }

        private final OutputStream ostream = System.out;

        {
            assertNotNull(ostream);
        }
    }

    @Test
    public void shouldHandleUnserializeableExceptions() throws Exception
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(event);
    }

    @Test
    public void shouldStoreExceptionClassesAsStrings()
    {
        assertEquals(UnserializableException.class.getSimpleName(), event.getErrorClassName());
    }

    @Test
    public void shouldPointOfFailure()
    {
        verifyPointOfFailureMessage(getLineNumber());
    }

    @Test
    public void shouldFilterJunitElementsFromPointOfFailure()
    {
        try
        {
            fail();
        }
        catch (AssertionError e)
        {
            error = e;
            event = eventWithError(e);
            verifyPointOfFailureMessage(e.getStackTrace()[2].getLineNumber());
        }
    }

    @Test
    public void shouldFilterJdaveElementsFromPointOfFailure()
    {
        error = JDaveUtils.createException();
        event = eventWithError(error);
        verifyPointOfFailureMessage(error.getStackTrace()[1].getLineNumber());
    }

    @Test
    public void shouldHaveUserPresentableToString()
    {
        assertEquals(event.getTestName() + "." + event.getTestMethod(), event.toString());
    }

    @Test
    public void shouldProvideFullErrorClassName()
    {
        assertEquals("java.lang.RuntimeException", methodFailed("", "", new RuntimeException()).getFullErrorClassName());
    }

    private int getLineNumber()
    {
        return error.getStackTrace()[0].getLineNumber();
    }

    private void verifyPointOfFailureMessage(int lineNumber)
    {
        String actual = event.getPointOfFailure().toString();

        String expected = TestEventTest.class.getName() + ":" + lineNumber + " - " + error.getClass().getSimpleName();
        String detailsJUnit48 = "(" + error.getMessage() + ")";
        String detailsJUnit49 = "(null)".equals(detailsJUnit48) ? "" : detailsJUnit48;

        assertTrue(actual.equals(expected + detailsJUnit48) || actual.equals(expected + detailsJUnit49));
    }

    @Override
    protected Object createEqualInstance()
    {
        return testCaseStarting(getClass().getName());
    }

    @Override
    protected Object createUnequalInstance()
    {
        return testCaseStarting(Object.class.getName());
    }

    @Override
    protected List<Object> createUnequalInstances()
    {
        List<Object> unequals = super.createUnequalInstances();
        unequals.add(methodFailed("boo", getClass().getName(), "testMethod", new RuntimeException()));
        return unequals;
    }

    private static TestEvent eventWithError(Throwable error)
    {
        return new TestEvent(METHOD_FAILURE, error.getMessage(), "", "", error);
    }
}
