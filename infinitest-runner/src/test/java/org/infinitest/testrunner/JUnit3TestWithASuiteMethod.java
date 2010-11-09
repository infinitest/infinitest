package org.infinitest.testrunner;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JUnit3TestWithASuiteMethod extends TestCase
{
    public void testShouldPass()
    {
    }

    public static junit.framework.Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new FailingJUnit3TestUsedBySuite());
        return suite;
    }
}
