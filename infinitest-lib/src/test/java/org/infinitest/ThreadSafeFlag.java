package org.infinitest;

import static org.junit.Assert.*;

public class ThreadSafeFlag
{
    private boolean tripped;
    private final long timeout;

    public ThreadSafeFlag(long timeout)
    {
        this.timeout = timeout;
    }

    public ThreadSafeFlag()
    {
        this(1000);
    }

    public synchronized void trip()
    {
        tripped = true;
        notify();
    }

    public synchronized void assertTripped() throws InterruptedException
    {
        if (!tripped)
            wait(timeout);
        assertTrue(tripped);
    }

}
