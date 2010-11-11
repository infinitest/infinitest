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

import static org.infinitest.Throwables.*;
import static org.infinitest.testrunner.TestEvent.TestState.*;

import java.io.Serializable;

/**
 * @author <a href="mailto:benrady@gmail.com">Ben Rady</a>
 */
public class TestEvent implements Serializable
{
    // Strange serialization errors occur (consistently) if we don't include this. I'm not sure why.
    private static final long serialVersionUID = -1821340797590868333L;

    public enum TestState
    {
        METHOD_FAILURE, TEST_CASE_STARTING
    }

    private final String message;
    private final String name;
    private final String method;
    private final TestState state;
    private boolean isAssertionFailure;
    private StackTraceElement[] stackTrace;
    private String simpleErrorClassName;
    private String fullErrorClassName;

    public TestEvent(TestState eventType, String message, String testName, String testMethod, Throwable error)
    {
        this.message = message;
        name = testName;
        method = testMethod;
        state = eventType;

        if (error != null)
        {
            populateAttributesToEnsureSerializability(message, error);
        }
    }

    public static TestEvent methodFailed(String message, String testName, String methodName, Throwable throwable)
    {
        return new TestEvent(METHOD_FAILURE, message, testName, methodName, throwable);
    }

    public static TestEvent methodFailed(String testName, String methodName, Throwable throwable)
    {
        return new TestEvent(METHOD_FAILURE, throwable.getMessage(), testName, methodName, throwable);
    }

    public static TestEvent testCaseStarting(String testClass)
    {
        return new TestEvent(TEST_CASE_STARTING, "Test Starting", testClass, "", null);
    }

    private void populateAttributesToEnsureSerializability(String errorMessage, Throwable error)
    {
        isAssertionFailure = isTestFailure(error);
        stackTrace = error.getStackTrace();
        simpleErrorClassName = error.getClass().getSimpleName();
        fullErrorClassName = error.getClass().getName();
    }

    public String getMessage()
    {
        return message;
    }

    public String getTestName()
    {
        return name;
    }

    public String getTestMethod()
    {
        return method;
    }

    public boolean isFailure()
    {
        return isAssertionFailure;
    }

    public TestState getType()
    {
        return state;
    }

    @Override
    public String toString()
    {
        return name + "." + method;
    }

    private String getPointOfFailureClass()
    {
        return getPointOfFailureElement().getClassName();
    }

    private StackTraceElement getPointOfFailureElement()
    {
        int i = 0;
        while (isTestClass(stackTrace[i].getClassName()))
        {
            i++;
        }
        return stackTrace[i];
    }

    private boolean isTestClass(String className)
    {
        return className.startsWith("org.junit") || className.startsWith("junit.framework")
                        || className.startsWith("jdave");
    }

    private int getPointOfFailureLineNumber()
    {
        return getPointOfFailureElement().getLineNumber();
    }

    public PointOfFailure getPointOfFailure()
    {
        if (getErrorClassName() != null)
        {
            return new PointOfFailure(getPointOfFailureClass(), getPointOfFailureLineNumber(), getErrorClassName(),
                            getMessage());
        }
        return null;
    }

    @Override
    public int hashCode()
    {
        // Can't use generateEquals here because this class needs to remain serializable, and
        // InfinitestUtils uses 3rd party libraries
        return name.hashCode() ^ method.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof TestEvent)
        {
            TestEvent other = (TestEvent) obj;
            return safeEquals(name, other.name) && safeEquals(method, other.method);
        }
        return false;
    }

    // Can't use infinitestUtils.safeEquals here because this class needs to remain serializable,
    // and InfinitestUtils uses 3rd party libraries
    private static boolean safeEquals(Object orig, Object other)
    {
        if (orig == null && other == null)
        {
            return true;
        }
        if (orig == null || other == null)
        {
            return false;
        }

        return orig.equals(other);
    }

    public String getErrorClassName()
    {
        return simpleErrorClassName;
    }

    public String getFullErrorClassName()
    {
        return fullErrorClassName;
    }

    public StackTraceElement[] getStackTrace()
    {
        return stackTrace;
    }
}
