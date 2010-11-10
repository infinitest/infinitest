package org.infinitest.testrunner;

import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestCaseFacade;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * JUnit38ClassRunner is not extensible, so I had to make a copy :-(
 * 
 * @author bjrady
 */
public class UninstantiateableJUnit3TestRunner extends Runner
{
    private static final class OldTestClassAdaptingListener implements TestListener
    {
        private final RunNotifier fNotifier;
        private final Class<?> testClass;

        private OldTestClassAdaptingListener(RunNotifier notifier, Class<?> testClass)
        {
            fNotifier = notifier;
            this.testClass = testClass;
        }

        public void endTest(Test test)
        {
            fNotifier.fireTestFinished(asDescription(test));
        }

        public void startTest(Test test)
        {
            fNotifier.fireTestStarted(asDescription(test));
        }

        public void addError(Test test, Throwable t)
        {
            Failure failure = new Failure(asDescription(test), t);
            fNotifier.fireTestFailure(failure);
        }

        private Description asDescription(Test test)
        {
            if (test instanceof JUnit4TestCaseFacade)
            {
                JUnit4TestCaseFacade facade = (JUnit4TestCaseFacade) test;
                return facade.getDescription();
            }
            return Description.createTestDescription(testClass, "<init>");
        }

        public void addFailure(Test test, AssertionFailedError t)
        {
            addError(test, t);
        }
    }

    private Test fTest;
    private Class<?> testClass;

    public UninstantiateableJUnit3TestRunner(Class<?> klass)
    {
        super();
        fTest = new TestSuite(klass.asSubclass(TestCase.class));
        this.testClass = klass;
    }

    @Override
    public void run(RunNotifier notifier)
    {
        TestResult result = new TestResult();
        result.addListener(createAdaptingListener(notifier, testClass));
        fTest.run(result);
    }

    public static TestListener createAdaptingListener(RunNotifier notifier, Class<?> testClass)
    {
        return new OldTestClassAdaptingListener(notifier, testClass);
    }

    @Override
    public Description getDescription()
    {
        return Description.EMPTY;
    }
}
