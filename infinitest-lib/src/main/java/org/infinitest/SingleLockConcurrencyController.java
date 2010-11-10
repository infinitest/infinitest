package org.infinitest;

import java.util.concurrent.Semaphore;

public class SingleLockConcurrencyController implements ConcurrencyController
{
    private Semaphore semaphore = new Semaphore(1);

    public void acquire() throws InterruptedException
    {
        semaphore.acquire();
    }

    public void release()
    {
        semaphore.release();
    }

    public void setCoreCount(int coreCount)
    {
        // Ignored...only 1 core supported
    }
}
