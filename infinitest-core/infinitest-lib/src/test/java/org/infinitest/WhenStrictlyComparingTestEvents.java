package org.infinitest;

import static org.hamcrest.Matchers.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.testrunner.TestEvent;
import org.infinitest.toolkit.EqualsHashCodeTestSupport;
import org.junit.Before;
import org.junit.Test;

public class WhenStrictlyComparingTestEvents extends EqualsHashCodeTestSupport
{
    private Throwable throwable;
    private TestEventEqualityAdapter event1;
    private TestEventEqualityAdapter event2;

    @Before
    public void inContext() throws Exception
    {
        throwable = new Throwable();
        throwable.fillInStackTrace();
        event1 = equal();
        event2 = notEqual();
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
    protected TestEventEqualityAdapter equal() throws Exception
    {
        return adapterFor(methodFailed("message1", "testName", "methodName", throwable));
    }

    @Override
    protected TestEventEqualityAdapter notEqual() throws Exception
    {
        return adapterFor(methodFailed("message2", "testName", "methodName", throwable));
    }
}
