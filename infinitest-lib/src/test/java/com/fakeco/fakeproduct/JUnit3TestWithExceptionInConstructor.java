package com.fakeco.fakeproduct;

import static org.infinitest.util.InfinitestTestUtils.*;
import junit.framework.TestCase;

public class JUnit3TestWithExceptionInConstructor extends TestCase
{
    public JUnit3TestWithExceptionInConstructor()
    {
        if (testIsBeingRunFromInfinitest())
        {
            throw new NullPointerException();
        }
    }

    public void testThatPasses()
    {
        assertTrue(true);
    }
}
