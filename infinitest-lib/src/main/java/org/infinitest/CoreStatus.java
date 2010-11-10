package org.infinitest;

/**
 * Represents the status of a core.
 */
public enum CoreStatus
{
    /**
     * Looking for tests to run. Core will stay in this state if there are no tests detected.
     */
    SCANNING,

    /**
     * Currently running tests. When in this state, the test queue will not be empty.
     */
    RUNNING,

    /**
     * At least one test run has completed with all tests passing.
     */
    PASSING,

    /**
     * At least one test run has completed with at least one test failing.
     */
    FAILING,

    /**
     * Building the class index. This is the initial state of the core.
     */
    INDEXING;
}
