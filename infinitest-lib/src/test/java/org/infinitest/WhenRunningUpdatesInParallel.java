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
