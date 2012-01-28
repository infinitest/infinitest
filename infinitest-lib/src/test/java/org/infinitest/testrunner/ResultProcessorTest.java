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
package org.infinitest.testrunner;

import static org.infinitest.testrunner.TestEvent.TestState.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.infinitest.*;
import org.infinitest.testrunner.process.*;
import org.junit.*;

public class ResultProcessorTest {
	private TestQueueProcessor reader;

	private ProcessConnectionFactory factory;
	private EventSupport eventAssert;
	private RunnerEventSupport runnerEventSupport;

	private ProcessConnection connection;

	@Before
	public void inContext() throws IOException {
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
	public void shouldRunGivenTest() throws Exception {
		reader.process("test1");
		reader.close();

		eventAssert.assertTestsStarted("test1");
		eventAssert.assertTestPassed("test1");
		eventAssert.assertRunComplete();

		verify(connection).runTest("test1");
	}

	@Test
	public void shouldOnlyOpenOneConnection() throws Exception {
		when(connection.runTest("test2")).thenReturn(new TestResults());

		reader.process("test1");
		reader.process("test2");
		reader.close();
		eventAssert.assertRunComplete();

		verify(factory, times(1)).getConnection(any(RuntimeEnvironment.class), any(OutputStreamHandler.class));
	}

	@Test
	public void shouldFireStartingEventBeforeTestStarts() throws Exception {
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
