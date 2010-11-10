package org.infinitest.testrunner;

/**
 * Implemementers of this interface must provide a default constructor so that the TestRunner can be
 * created using Class.newInstance();
 *
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public interface NativeRunner
{
    TestResults runTest(String testClass);
}
