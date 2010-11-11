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
package org.infinitest.util;

import static org.infinitest.testrunner.TestEvent.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;
import junit.framework.AssertionFailedError;

import org.infinitest.testrunner.TestEvent;

public class EventFakeSupport
{
    public static final String FAILURE_MSG = "Test failed as expected";
    public static final String TEST_NAME = "com.fakeco.TestFoo";
    public static final String TEST_METHOD = "someMethod";

    public static TestEvent withFailingMethod(String methodName)
    {
        return createEvent(methodName, FAILURE_MSG);
    }

    public static TestEvent withTestNamed(String testName)
    {
        return testCaseStarting(testName);
    }

    public static TestEvent createEvent(String methodName, Throwable error)
    {
        return new TestEvent(METHOD_FAILURE, error.getMessage(), TEST_NAME, methodName, error);
    }

    public static TestEvent createEvent(String methodName, String msg)
    {
        String testMsg = "testFailed(" + msg + ")";
        return new TestEvent(METHOD_FAILURE, testMsg, TEST_NAME, methodName, new AssertionFailedError());
    }

    public static TestEvent eventWithError(Throwable error)
    {
        return new TestEvent(METHOD_FAILURE, error.getMessage(), TEST_NAME, TEST_METHOD, error);
    }

    public static TestEvent withATest()
    {
        return new TestEvent(TEST_CASE_STARTING, "", TEST_NAME, TEST_METHOD, null);
    }

    public static TestEvent withTest(Class<?> testClass)
    {
        return withTestNamed(testClass.getName());
    }
}
