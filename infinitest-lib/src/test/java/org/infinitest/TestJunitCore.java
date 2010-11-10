package org.infinitest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Exploratory test for JunitCore
 * 
 * @author bjrady
 * 
 */
public class TestJunitCore
{
    private List<Description> finishedList;
    private JUnitCore core;
    private ArrayList<Failure> failingList;

    @Before
    public void whenCoreHasListeners()
    {
        core = new JUnitCore();
        finishedList = new ArrayList<Description>();
        failingList = new ArrayList<Failure>();
        core.addListener(new RunListener()
        {
            @Override
            public void testFinished(Description description)
            {
                finishedList.add(description);
            }

            @Override
            public void testFailure(Failure failure)
            {
                failingList.add(failure);
            }
        });
        StubTest.enable();
        core.run(new Class[] { StubTest.class });
    }

    @After
    public void cleanup()
    {
        core = null;
        finishedList = null;
        StubTest.disable();
    }

    @Test
    public void shouldRunTestsAndReportResults()
    {
        Description desc = findDescription("shouldBeSucessful", finishedList);
        assertEquals("Display name", "shouldBeSucessful(org.infinitest.TestJunitCore$StubTest)", desc.getDisplayName());
        assertTrue("Result should have no children", desc.getChildren().isEmpty());
        assertTrue("Test result, not suite result", desc.isTest());
        assertEquals("Test Count", 1, desc.testCount());

        desc = findDescription("shouldFailIfPropertyIsSet", finishedList);
        assertEquals("Display name", "shouldFailIfPropertyIsSet(org.infinitest.TestJunitCore$StubTest)",
                        desc.getDisplayName());
        assertTrue("Result should have no children", desc.getChildren().isEmpty());
        assertTrue("Test result, not suite result", desc.isTest());
        assertEquals("Test Count", 1, desc.testCount());

        assertEquals(2, finishedList.size());
    }

    private static Description findDescription(String methodName, List<Description> testList)
    {
        for (Description d : testList)
        {
            if (d.getDisplayName().startsWith(methodName + "("))
            {
                return d;
            }
        }
        return null;
    }

    @Test
    public void shouldNotifyListenersAboutFailingTests()
    {
        assertEquals(1, failingList.size());
        Failure failure = failingList.get(0);
        assertEquals("shouldFailIfPropertyIsSet(org.infinitest.TestJunitCore$StubTest)", failure.getTestHeader());
        assertEquals("This test should fail", failure.getMessage());
    }

    public static class StubTest
    {
        @Test
        @SuppressWarnings("all")
        public void shouldBeSucessful()
        {
        }

        @Test
        public void shouldFailIfPropertyIsSet()
        {
            // This is done so Eclipse doesn't get confused and fail because of this test.
            if (System.getProperty(StubTest.class.getName()) != null)
            {
                fail("This test should fail");
            }
        }

        public static void enable()
        {
            System.setProperty(StubTest.class.getName(), "");
        }

        public static void disable()
        {
            System.clearProperty(StubTest.class.getName());
        }
    }
}
