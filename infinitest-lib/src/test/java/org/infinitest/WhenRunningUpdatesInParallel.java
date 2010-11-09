package org.infinitest;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhenRunningUpdatesInParallel
{
    @Test(timeout = 1000)
    public void shouldLimitTheNumberOfConcurrentUpdates() throws InterruptedException
    {
        MultiCoreConcurrencyController controller = new MultiCoreConcurrencyController(1, 3);
        assertEquals(1, controller.availablePermits());
        controller.acquire();
        assertEquals(0, controller.availablePermits());

        controller.setCoreCount(3);
        assertEquals(2, controller.availablePermits());

        controller.acquire();
        assertEquals(1, controller.availablePermits());
    }

    @Test(timeout = 1000)
    public void shouldNotBlockWhenCoreCountIsIncreased() throws InterruptedException
    {
        MultiCoreConcurrencyController controller = new MultiCoreConcurrencyController(1, 2);
        controller.acquire();
        assertEquals(0, controller.availablePermits());

        controller.setCoreCount(2);
        assertEquals(1, controller.availablePermits());

        controller.release();
        assertEquals(2, controller.availablePermits());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMaxIsSetTooHigh()
    {
        MultiCoreConcurrencyController controller = new MultiCoreConcurrencyController(1, 1);
        controller.setCoreCount(2);
    }

    @Test(timeout = 1000)
    public void shouldIgnoreChangeIfNotEnoughPermits() throws InterruptedException
    {
        MultiCoreConcurrencyController controller = new MultiCoreConcurrencyController(3, 3);
        controller.acquire();
        controller.acquire();
        controller.acquire();
        controller.setCoreCount(1);
        assertEquals(0, controller.availablePermits());
        assertEquals(3, controller.getCoreCount());
    }
}
