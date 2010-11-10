package com.fakeco.fakeproduct;

import junit.framework.TestCase;

public class TestJunit3TestCase extends TestCase
{
    public TestJunit3TestCase(String name)
    {
        super(name);
    }

    public void testTrueIsTrue()
    {
        assertTrue(true);
    }
}
