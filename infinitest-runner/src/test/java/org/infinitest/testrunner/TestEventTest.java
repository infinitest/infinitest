package org.infinitest.testrunner;

import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.EventFakeSupport.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import jdave.test.JDaveUtils;

import org.infinitest.util.EqualityTestSupport;
import org.infinitest.util.EventFakeSupport;
import org.junit.Before;
import org.junit.Test;

//Move this to infinitest-runners
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
            event = EventFakeSupport.eventWithError(e);
            verifyPointOfFailureMessage(e.getStackTrace()[2].getLineNumber());
        }
    }

    @Test
    public void shouldFilterJdaveElementsFromPointOfFailure()
    {
        error = JDaveUtils.createException();
        event = EventFakeSupport.eventWithError(error);
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
        String expected = TestEventTest.class.getName() + ":" + lineNumber + " - " + error.getClass().getSimpleName()
                        + "(" + error.getMessage() + ")";

        assertEquals(expected, actual);
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
}
