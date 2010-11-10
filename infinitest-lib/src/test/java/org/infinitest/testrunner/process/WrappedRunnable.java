package org.infinitest.testrunner.process;

public abstract class WrappedRunnable implements Runnable
{
    public void run()
    {
        try
        {
            runWrapped();
        }
        catch (Exception swallowed)
        {
            // Used in a test where runWrapped has a finally block to handle the exception.
            // At this point, we don't care about the exception anymore
        }
    }

    protected abstract void runWrapped() throws Exception;
}
