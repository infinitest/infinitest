package org.infinitest;

public interface ConcurrencyController
{
    void acquire() throws InterruptedException;

    void release();

    void setCoreCount(int coreCount);
}
