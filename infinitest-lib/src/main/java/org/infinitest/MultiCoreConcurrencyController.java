/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest;

import java.util.concurrent.*;

public class MultiCoreConcurrencyController implements ConcurrencyController {
	public static final int DEFAULT_MAX_CORES = 8;

	private int coreCount;
	private final Semaphore semaphore;
	private final int maxNumberOfPermits;

	public MultiCoreConcurrencyController() {
		this(1, DEFAULT_MAX_CORES);
	}

	public MultiCoreConcurrencyController(int intitalCoreCount, int numberOfProcessors) {
		maxNumberOfPermits = numberOfProcessors;
		semaphore = new Semaphore(numberOfProcessors);
		semaphore.acquireUninterruptibly(numberOfProcessors - intitalCoreCount);
		coreCount = intitalCoreCount;
	}

	public void acquire() throws InterruptedException {
		semaphore.acquire();
	}

	public void release() {
		semaphore.release();
	}

	public synchronized void setCoreCount(int newCoreCount) {
		if (newCoreCount > maxNumberOfPermits) {
			throw new IllegalArgumentException("Settings core count to " + newCoreCount + " max is " + maxNumberOfPermits);
		}
		releaseNecessaryPermits(newCoreCount);
		acquireNecessaryPermits(newCoreCount);
	}

	private void acquireNecessaryPermits(int newCoreCount) {
		if (newCoreCount < coreCount) {
			if (semaphore.tryAcquire(coreCount - newCoreCount)) {
				coreCount = newCoreCount;
			}
		}
	}

	private void releaseNecessaryPermits(int newCoreCount) {
		if (newCoreCount > coreCount) {
			semaphore.release(newCoreCount - coreCount);
			coreCount = newCoreCount;
		}
	}

	int availablePermits() {
		return semaphore.availablePermits();
	}

	int getCoreCount() {
		return coreCount;
	}
}
