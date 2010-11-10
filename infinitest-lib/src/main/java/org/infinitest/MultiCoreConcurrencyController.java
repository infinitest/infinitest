package org.infinitest;

import java.util.concurrent.Semaphore;

public class MultiCoreConcurrencyController implements ConcurrencyController
{
    public static final int DEFAULT_MAX_CORES = 8;

    private int coreCount;
    private final Semaphore semaphore;
    private final int maxNumberOfPermits;

    public MultiCoreConcurrencyController()
    {
        this(1, DEFAULT_MAX_CORES);
    }

    public MultiCoreConcurrencyController(int intitalCoreCount, int numberOfProcessors)
    {
        this.maxNumberOfPermits = numberOfProcessors;
        semaphore = new Semaphore(numberOfProcessors);
        semaphore.acquireUninterruptibly(numberOfProcessors - intitalCoreCount);
        coreCount = intitalCoreCount;
    }

    public void acquire() throws InterruptedException
    {
        semaphore.acquire();
    }

    public void release()
    {
        semaphore.release();
    }

    public synchronized void setCoreCount(int newCoreCount)
    {
        if (newCoreCount > maxNumberOfPermits)
        {
            throw new IllegalArgumentException("Settings core count to " + newCoreCount + " max is "
                            + maxNumberOfPermits);
        }
        releaseNecessaryPermits(newCoreCount);
        acquireNecessaryPermits(newCoreCount);
    }

    private void acquireNecessaryPermits(int newCoreCount)
    {
        if (newCoreCount < this.coreCount)
        {
            if (semaphore.tryAcquire(coreCount - newCoreCount))
            {
                coreCount = newCoreCount;
            }
        }
    }

    private void releaseNecessaryPermits(int newCoreCount)
    {
        if (newCoreCount > this.coreCount)
        {
            semaphore.release(newCoreCount - coreCount);
            coreCount = newCoreCount;
        }
    }

    int availablePermits()
    {
        return semaphore.availablePermits();
    }

    int getCoreCount()
    {
        return coreCount;
    }
}
