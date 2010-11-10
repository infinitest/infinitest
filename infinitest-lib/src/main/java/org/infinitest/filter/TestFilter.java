package org.infinitest.filter;


/**
 * This filter prevents any matching tests from being run as part of a core update.
 * 
 * @author bjrady
 */
public interface TestFilter
{
    /**
     * Forces an update of the filter, if controlled by an external resource. This may be necessary
     * if a test class has been removed or added from the dependency graph.
     */
    void updateFilterList();

    /**
     * Check if a test class (identified by the fully qualified name) should be removed from the
     * test run.
     * 
     * @return <code>true</code> if the test should not be run
     * @see Class#getName()
     */
    boolean match(String className);
}