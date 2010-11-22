/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
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

import java.util.LinkedList;
import java.util.List;

import org.infinitest.TestQueueAdapter;
import org.infinitest.util.ThreadSafeFlag;
import org.junit.Test;

import com.fakeco.fakeproduct.TestJUnit4TestCase;

public abstract class AbstractRunnerTest
{
    private ThreadSafeFlag runComplete;

    @Test
    public void shouldFireEventWhenTestRunIsComplete() throws Exception
    {
        runComplete = new ThreadSafeFlag();
        getRunner().addTestQueueListener(new TestQueueAdapter()
        {
            @Override
            public void testRunComplete()
            {
                runComplete.trip();
            }
        });
        getRunner().runTest(TestJUnit4TestCase.class.getName());
        runComplete.assertTripped();
    }

    protected void runTest(String testName) throws InterruptedException
    {
        getRunner().runTest(testName);
        waitForCompletion();
    }

    protected void runTests(Class<?>... tests) throws InterruptedException
    {
        List<String> testNames = new LinkedList<String>();
        for (Class<?> each : tests)
        {
            testNames.add(each.getName());
        }
        getRunner().runTests(testNames);
        waitForCompletion();
    }

    protected abstract void waitForCompletion() throws InterruptedException;

    protected abstract AbstractTestRunner getRunner();

}