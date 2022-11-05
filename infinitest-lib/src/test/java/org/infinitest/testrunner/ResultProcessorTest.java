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

import static org.infinitest.testrunner.TestEvent.TestState.TEST_CASE_STARTING;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.infinitest.EventSupport;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.testrunner.process.ProcessConnection;
import org.infinitest.testrunner.process.ProcessConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResultProcessorTest {
	private TestQueueProcessor reader;

	private ProcessConnectionFactory factory;
	private EventSupport eventAssert;
	private RunnerEventSupport runnerEventSupport;

	private ProcessConnection connection;

	@BeforeEach
	void inContext() throws IOException {
		eventAssert = new EventSupport();
		factory = mock(ProcessConnectionFactory.class);
		runnerEventSupport = new RunnerEventSupport(this);
		runnerEventSupport.addTestQueueListener(eventAssert);
		runnerEventSupport.addTestStatusListener(eventAssert);
		connection = mock(ProcessConnection.class);
		when(factory.getConnection((RuntimeEnvironment) isNull(), any(OutputStreamHandler.class))).thenReturn(connection);
		reader = new TestQueueProcessor(runnerEventSupport, factory, null);
		connection.close();

		when(connection.runTest("test1")).thenReturn(new TestResults());
	}

	@Test
	void shouldRunGivenTest() throws Exception {
		reader.process("test1");
		reader.close();

		eventAssert.assertTestsStarted("test1");
		eventAssert.assertTestPassed("test1");
		eventAssert.assertRunComplete();

		verify(connection).runTest("test1");
	}

	@Test
	void shouldOnlyOpenOneConnection() throws Exception {
		when(connection.runTest("test2")).thenReturn(new TestResults());

		reader.process("test1");
		reader.process("test2");
		reader.close();
		eventAssert.assertRunComplete();

		verify(factory, times(1)).getConnection(isNull(), any(OutputStreamHandler.class));
	}

	@Test
	void shouldFireStartingEventBeforeTestStarts() throws Exception {
		when(connection.runTest("test2")).thenThrow(new RuntimeException());
		try {
			reader.process("test2");
			fail("shouldHaveThrownException");
		} catch (RuntimeException expected) {
			// ok
		}

		reader.close();

		eventAssert.assertEventsReceived(TEST_CASE_STARTING);
		eventAssert.assertRunComplete();
		verify(connection).runTest("test2");
	}
}
