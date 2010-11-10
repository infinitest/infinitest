package org.infinitest.testrunner;

import static org.junit.Assert.*;

import org.junit.Test;

public class FailingTest
{
    public static boolean fail;

    @Test
    public void shouldFail()
    {
        assertFalse(fail);
    }
}
