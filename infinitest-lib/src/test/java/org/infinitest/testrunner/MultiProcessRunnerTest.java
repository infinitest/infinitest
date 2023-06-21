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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinitest.ConcurrencyController;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.testrunner.process.ProcessConnection;
import org.infinitest.testrunner.process.ProcessConnectionFactory;
import org.junit.jupiter.api.Test;

/**
 * @author gtoison
 *
 */
class MultiProcessRunnerTest {

	@Test
	void stopShouldKillRunningTest() throws InterruptedException, IOException {
		ProcessConnectionFactory remoteProcessManager = mock(ProcessConnectionFactory.class);
		ProcessConnection processConnection = mock(ProcessConnection.class);
		RuntimeEnvironment environment  = mock(RuntimeEnvironment.class);
		ConcurrencyController semaphore = mock(ConcurrencyController.class);
		
		when(remoteProcessManager.getConnection(any(), any())).thenReturn(processConnection);

		AtomicBoolean testStarting = new AtomicBoolean(false);
		CountDownLatch testStarted = new CountDownLatch(1);
		AtomicBoolean testCompleted = new AtomicBoolean(false);
		
		when(processConnection.runTest(anyString())).then(i -> {
			testStarted.countDown();
			Thread.sleep(60000L);
			testCompleted.set(true);
			
			return null;
		});
		
		
		MultiProcessRunner runner = new MultiProcessRunner(remoteProcessManager, environment);
		runner.setConcurrencyController(semaphore);
		runner.addTestResultsListener(new TestResultsListener() {
			
			@Override
			public void testCaseStarting(TestEvent event) {
				testStarting.set(true);
			}
			
			@Override
			public void testCaseComplete(TestCaseEvent event) {
				
			}
		});
		
		runner.runTest("com.test.ExampleTest");
		
		// The test runs on another thread, wait for it to start or our stop() call will not stop anything
		// It should take a lot less than 10 seconds for the test to start
		testStarted.await(10, TimeUnit.SECONDS);
		
		runner.stop();

		assertThat(testStarting).isTrue();
		assertThat(testCompleted).isFalse();
	}
}
