package org.infinitest.testrunner;

import org.junit.Test;

@SuppressWarnings("all")
public class TestThatThrowsExceptionInConstructor
{
    public static boolean fail;

    public TestThatThrowsExceptionInConstructor()
    {
        if (fail)
        {
            throw new IllegalStateException();
        }
    }

    @Test
    public void shouldPass()
    {
    }
}