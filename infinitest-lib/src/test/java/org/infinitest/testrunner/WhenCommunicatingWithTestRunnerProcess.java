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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.infinitest.testrunner.TestEvent.testCaseStarting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import org.infinitest.testrunner.process.TcpSocketProcessCommunicator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class WhenCommunicatingWithTestRunnerProcess {
	private TcpSocketProcessCommunicator communicator;

	@BeforeEach
	void inContext() {
		communicator = new TcpSocketProcessCommunicator();
	}

	@AfterEach
	void cleanup() {
		communicator.closeSocket();
	}

	@Test
	void shouldOpenAPort() {
		assertThat(communicator.createSocket()).as("Port cannot be zero").isNotEqualTo(0);
	}

	@Test
	void shouldThrowExceptionIfTwoSocketsAreCreated() {
		communicator.createSocket();
		assertThatThrownBy(() -> communicator.createSocket()).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void shouldReadResults() {
		final int portNum = communicator.createSocket();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (Socket clientSocket = new Socket("127.0.0.1", portNum);
					ObjectOutputStream ooStream = new ObjectOutputStream(clientSocket.getOutputStream());
					BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {
					assertThat(inStream.readLine()).isEqualTo("hello");
					ooStream.writeObject(new TestResults(testCaseStarting("hello")));
					assertThat(inStream.readLine()).isNull();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		communicator.openSocket();
		assertThat(communicator.sendMessage("hello")).hasSize(1);
		communicator.closeSocket();
	}

	@Timeout(1)
	@Test
	void shouldTimeOutIfRunnerProcessFailsToStart() {
		communicator = new TcpSocketProcessCommunicator(250);
		communicator.createSocket();
		try {
			communicator.openSocket();
		} catch (RuntimeException e) {
			assertThat(e.getCause()).isInstanceOf(SocketTimeoutException.class);
		}
	}
}
