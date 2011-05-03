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

import static org.junit.runner.Request.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.infinitest.MissingClassException;
import org.infinitest.TestNGConfiguration;
import org.infinitest.TestNGConfigurator;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;

public class JUnit4Runner implements NativeRunner
{
    private TestNGConfiguration config = null;

    public TestResults runTest(String testClass)
    {
        Class<?> clazz;
        try
        {
            clazz = Class.forName(testClass);
        }
        catch (ClassNotFoundException e)
        {
            throw new MissingClassException(testClass);
        }

        if (isTestNGTest(clazz))
        {
            TestNG core = new TestNG();
            TestNGEventTranslator eventTranslator = new TestNGEventTranslator();
            core.addListener(eventTranslator);

            core.setTestClasses(new Class[] { clazz });

            addGroupFilters(core);
            core.run();

            return eventTranslator.getTestResults();
        }

        JUnitCore core = new JUnitCore();
        EventTranslator eventTranslator = new EventTranslator();
        core.addListener(eventTranslator);

        if (isJUnit3TestCase(clazz) && cannotBeInstantiated(clazz))
        {
            core.run(new UninstantiableJUnit3TestRequest(clazz));
        }
        else
        {
            core.run(classWithoutSuiteMethod(clazz));
        }
        return eventTranslator.getTestResults();
    }

    private void addGroupFilters(TestNG core)
    {
        if (config == null)
        {
            config = new TestNGConfigurator().getConfig();
        }
        if (config.getExcludedGroups() != null)
        {
            core.setExcludedGroups(config.getExcludedGroups());
        }
        if (config.getGroups() != null)
        {
            core.setGroups(config.getGroups());
        }
    }

    private boolean isJUnit3TestCase(Class<?> clazz)
    {
        return TestCase.class.isAssignableFrom(clazz);
    }

    private boolean cannotBeInstantiated(Class<?> clazz)
    {
        CustomTestSuite testSuite = new CustomTestSuite(clazz.asSubclass(TestCase.class));
        return testSuite.hasWarnings();
    }

    private static class CustomTestSuite extends TestSuite
    {
        public CustomTestSuite(Class<? extends TestCase> testClass)
        {
            super(testClass);
        }

        private boolean hasWarnings()
        {
            for (Enumeration<Test> tests = tests(); tests.hasMoreElements();)
            {
                Test test = tests.nextElement();
                if (test instanceof TestCase)
                {
                    TestCase testCase = (TestCase) test;
                    if (testCase.getName().equals("warning"))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class UninstantiableJUnit3TestRequest extends Request
    {
        private final Class<?> testClass;

        public UninstantiableJUnit3TestRequest(Class<?> clazz)
        {
            testClass = clazz;
        }

        @Override
        public Runner getRunner()
        {
            return new UninstantiateableJUnit3TestRunner(testClass);
        }
    }

    private boolean isTestNGTest(Class<?> clazz)
    {
        for (Method method : clazz.getMethods())
        {
            for (Annotation annotation : method.getAnnotations())
            {
                if (annotation.annotationType() == org.testng.annotations.Test.class)
                {
                    return true;
                }
            }
        }
        return false;
    }

    static class TestNGEventTranslator implements ITestListener
    {
        private final List<TestEvent> eventsCollected = new ArrayList<TestEvent>();

        public void onTestStart(ITestResult result)
        {
        }

        public void onTestSuccess(ITestResult result)
        {
        }

        public void onTestFailure(ITestResult failure)
        {
            eventsCollected.add(createEventFrom(failure));
        }

        private TestEvent createEventFrom(ITestResult failure)
        {
            return TestEvent.methodFailed(failure.getTestClass().getName(), failure.getName(), failure.getThrowable());
        }

        public TestResults getTestResults()
        {
            return new TestResults(eventsCollected);
        }

        public void onTestSkipped(ITestResult result)
        {
        }

        public void onTestFailedButWithinSuccessPercentage(ITestResult result)
        {
        }

        public void onStart(ITestContext context)
        {
        }

        public void onFinish(ITestContext context)
        {
        }
    }

    public void setTestNGConfiguration(TestNGConfiguration configuration)
    {
        config = configuration;
    }
}
