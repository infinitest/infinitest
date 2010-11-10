package com.fakeco.fakeproduct;

import static org.infinitest.util.InfinitestTestUtils.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
    public AllTests(String testName)
    {
        super(testName);
    }

    public static void main(String args[])
    {
        String[] testCaseName = { AllTests.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        if (testIsBeingRunFromInfinitest())
            fail("Should not try to run this");

        return suite;
    }
}
