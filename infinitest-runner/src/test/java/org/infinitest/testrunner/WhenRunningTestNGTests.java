/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2011
 * "Matthias Droste" <matthias.droste@gmail.com>,
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.testrunner;

import static com.google.common.collect.Iterables.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.infinitest.TestNGConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenRunningTestNGTests
{
    private JUnit4Runner runner;
    private static final String CLASS_UNDER_TEST = TestWithTestNGGroups.class.getName();
    private TestNGConfiguration config;

    @Before
    public void inContext()
    {
        runner = new JUnit4Runner();
        config = new TestNGConfiguration();
        runner.setTestNGConfiguration(config);
        TestWithTestNGGroups.fail = true;
        TestWithTestNGGroups.dependencyFail = true;
    }

    @After
    public void cleanup()
    {
        TestWithTestNGGroups.fail = false;
        TestWithTestNGGroups.dependencyFail = false;
        runner.setTestNGConfiguration(null);
    }

    /**
     * no test filters set: bad tests fail. But: the dependent test
     * "shouldNoBeTestedDueToDependencyOnFilteredGroup" is not executed
     */
    @Test
    public void shouldFailIfBadTestsAreNotFiltered()
    {
        final Set<String> failingMethods = new HashSet<String>(Arrays.asList("shouldNotBeTestedGroup",
                        "shouldNotBeTestedGroup3", "shouldNotBeTestedGroup2"));
        TestResults results = runner.runTest(CLASS_UNDER_TEST);
        int counter = 0;
        for (TestEvent testEvent : results)
        {
            counter++;
            assertTrue(failingMethods.contains(testEvent.getTestMethod()));
            assertEquals(AssertionError.class.getName(), testEvent.getFullErrorClassName());
            assertEquals(CLASS_UNDER_TEST, testEvent.getTestName());
        }
        assertEquals(failingMethods.size(), counter);
    }

    @Test
    public void shouldExecuteDependentTestIfMasterGroupWorked()
    {
        TestWithTestNGGroups.fail = false;
        TestResults results = runner.runTest(CLASS_UNDER_TEST);
        TestEvent testEvent = getOnlyElement(results);
        assertEquals("shouldNoBeTestedDueToDependencyOnFilteredGroup", testEvent.getTestMethod());
        assertEquals(AssertionError.class.getName(), testEvent.getFullErrorClassName());
    }

    @Test
    public void shouldNotFailWithFilteredGroupsSet()
    {
        config.setExcludedGroups("slow, manual");
        TestResults results = runner.runTest(CLASS_UNDER_TEST);
        assertEquals(0, size(results));
    }

    @Test
    public void shouldExecuteOnlyTheSpecifiedGroup()
    {
        config.setGroups("slow");
        TestResults results = runner.runTest(CLASS_UNDER_TEST);
        assertEquals(2, size(results));

        config.setGroups("shouldbetested");
        results = runner.runTest(CLASS_UNDER_TEST);
        assertEquals(0, size(results));
    }

    /**
     * if the group "slow" is included, but "mixed" excluded, a test with groups = "mixed", "slow"
     * will not be executed
     */
    @Test
    public void combineIncludedAndExcludedGroups()
    {
        config.setGroups("slow");
        config.setExcludedGroups("mixed");
        TestResults results = runner.runTest(CLASS_UNDER_TEST);
        assertEquals(1, size(results));
    }

}
