package org.infinitest.testrunner;

import junit.framework.TestCase;

public final class FailingJUnit3TestUsedBySuite extends TestCase
{
    FailingJUnit3TestUsedBySuite()
    {
        super("testShouldFail");
    }

    public void testShouldFail()
    {
        fail();
    }
}