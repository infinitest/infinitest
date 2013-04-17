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
package org.infinitest.testrunner.process;

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.FailingRunner.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.concurrent.*;

import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.testrunner.*;
import org.infinitest.util.*;
import org.junit.*;

public class NativeConnectionFactoryTest {
	protected String errorMsg;

	@Test
	public void shouldRunTests() throws Exception {
		NativeConnectionFactory manager = new NativeConnectionFactory(FailingRunner.class);
		ProcessConnection connection = manager.getConnection(fakeEnvironment(), new NoOpOutputHandler());
		Iterable<TestEvent> results = connection.runTest("testName");
		connection.close();
		assertEquals(FAILING_EVENT, getOnlyElement(results));
	}

	@Test
	public void canStopTestRun() throws Exception {
		final SynchronousQueue<String> testQueue = new SynchronousQueue<String>();
		NativeConnectionFactory manager = new NativeConnectionFactory(HangingRunner.class);
		final ProcessConnection connection = manager.getConnection(fakeEnvironment(), new NoOpOutputHandler());

		new Thread(new WrappedRunnable() {
			@Override
			protected void runWrapped() throws Exception {
				try {
					connection.runTest(testQueue.take());
				} finally {
					testQueue.put("finished");
				}
			}
		}).start();

		testQueue.put("test1");
		assertTrue(connection.abort());
		assertEquals("finished", testQueue.take());
	}

	@Test
	public void shouldPrintConsoleOutputEvenIfRunnerFailsToStart() throws Exception {
		NativeConnectionFactory manager = new NativeConnectionFactory(RunnerThatCannotBeCreated.class) {
			@Override
			protected TcpSocketProcessCommunicator createCommunicator() {
				return new TcpSocketProcessCommunicator(100);
			}
		};
		OutputStreamHandler outputListener = new OutputStreamHandler() {
			public void processStream(InputStream stream, OutputType type) {
				try {
					errorMsg = InfinitestTestUtils.toString(stream);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		try {
			manager.getConnection(fakeEnvironment(), outputListener);
			fail("Failed to throw error on test run failure");
		} catch (RuntimeException e) {
			assertTrue(errorMsg.contains(TestRunnerProcess.TEST_RUN_ERROR));
		}
	}
}
