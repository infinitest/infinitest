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
package org.infinitest.testrunner;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.testrunner.process.*;
import org.infinitest.testrunner.queue.*;

public class MultiProcessRunner extends AbstractTestRunner {
	private QueueConsumer queueConsumer;

	// DEBT Move into QueueConsumer.
	private Queue<String> queue;

	public MultiProcessRunner() {
		this(new NativeConnectionFactory(DefaultRunner.class), null);
	}

	public MultiProcessRunner(final ProcessConnectionFactory remoteProcessManager, RuntimeEnvironment environment) {
		queue = new TestQueue(getTestPriority());

		setRuntimeEnvironment(environment);
		queueConsumer = new QueueConsumer(getEventSupport(), queue) {
			@Override
			protected QueueProcessor createQueueProcessor() throws IOException {
				return new TestQueueProcessor(getEventSupport(), remoteProcessManager, getRuntimeEnvironment());
			}
		};
	}

	@Override
	public void setConcurrencyController(ConcurrencyController semaphore) {
		super.setConcurrencyController(semaphore);
		queueConsumer.setConcurrencySemaphore(getConcurrencySemaphore());
	}

	@Override
	public void runTests(List<String> testNames) {
		if (!testNames.isEmpty()) {
			queueConsumer.push(testNames);
		}
	}
}
