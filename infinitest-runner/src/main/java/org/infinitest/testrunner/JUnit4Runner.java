package org.infinitest.testrunner;

import static org.junit.runner.Request.*;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.infinitest.MissingClassException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class JUnit4Runner implements NativeRunner
{
    public TestResults runTest(String testClass)
    {
        JUnitCore core = new JUnitCore();
        EventTranslator eventTranslator = new EventTranslator();
        core.addListener(eventTranslator);
        try
        {
            Class<?> clazz = Class.forName(testClass);
            if (isJUnit3TestCase(clazz) && cannotBeInstantiated(clazz))
            {
                core.run(new UninstantiableJUnit3TestRequest(clazz));
            }
            else
            {
                core.run(classWithoutSuiteMethod(clazz));
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new MissingClassException(testClass);
        }
        return eventTranslator.getTestResults();
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
}
