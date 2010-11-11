/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
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
