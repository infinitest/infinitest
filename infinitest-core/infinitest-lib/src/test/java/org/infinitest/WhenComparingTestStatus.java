package org.infinitest;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhenComparingTestStatus
{
    @Test
    public void shouldBeEqual()
    {
        assertFalse(CoreStatus.FAILING.equals(CoreStatus.PASSING));
        assertTrue(CoreStatus.FAILING.equals(CoreStatus.FAILING));
    }
}
