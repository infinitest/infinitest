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

import static com.google.common.collect.Iterables.*;
import static org.infinitest.testrunner.TestEvent.*;
import static org.junit.Assert.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import org.infinitest.testrunner.process.*;
import org.junit.*;

public class WhenCommunicatingWithTestRunnerProcess {
	private TcpSocketProcessCommunicator communicator;

	@Before
	public void inContext() {
		communicator = new TcpSocketProcessCommunicator();
	}

	@After
	public void cleanup() {
		communicator.closeSocket();
	}

	@Test
	public void shouldOpenAPort() {
		assertFalse("Port cannot be zero", communicator.createSocket() == 0);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionIfTwoSocketsAreCreated() {
		communicator.createSocket();
		communicator.createSocket();
	}

	@Test
	public void shouldReadResults() {
		final int portNum = communicator.createSocket();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (Socket clientSocket = new Socket("127.0.0.1", portNum);
					ObjectOutputStream ooStream = new ObjectOutputStream(clientSocket.getOutputStream());
					BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {
					assertEquals("hello", inStream.readLine());
					ooStream.writeObject(new TestResults(testCaseStarting("hello")));
					assertNull(inStream.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		communicator.openSocket();
		assertEquals(1, size(communicator.sendMessage("hello")));
		communicator.closeSocket();
	}

	@Test(timeout = 1000)
	public void shouldTimeOutIfRunnerProcessFailsToStart() {
		communicator = new TcpSocketProcessCommunicator(250);
		communicator.createSocket();
		try {
			communicator.openSocket();
		} catch (RuntimeException e) {
			assertTrue(e.getCause().toString(), e.getCause() instanceof SocketTimeoutException);
		}
	}
}
