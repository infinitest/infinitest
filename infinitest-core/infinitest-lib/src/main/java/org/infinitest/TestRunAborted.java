package org.infinitest;

public class TestRunAborted extends RuntimeException
{
    private static final long serialVersionUID = -1L;

    private final String testName;

    public TestRunAborted(String testName, Exception e)
    {
        super("Test run was aborted", e);
        this.testName = testName;
    }

    public String getTestName()
    {
        return testName;
    }

}
