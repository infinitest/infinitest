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

import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;

public class TcpSocketProcessCommunicator {
	private ServerSocket serverSocket;
	private ObjectInputStream inStream;
	private PrintStream writer;
	private Socket socket;
	private final int timeout;

	public TcpSocketProcessCommunicator() {
		this(2500);
	}

	public TcpSocketProcessCommunicator(int timeout) {
		this.timeout = timeout;
	}

	public int createSocket() {
		if ((serverSocket != null) && !serverSocket.isClosed()) {
			throw new IllegalStateException("Test runner socket is already open");
		}

		try {
			serverSocket = new ServerSocket(0);
			serverSocket.setSoTimeout(timeout);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port for interprocess communication", e);
		}
		return serverSocket.getLocalPort();
	}

	public void openSocket() {
		try {
			socket = serverSocket.accept();
			log(Level.CONFIG, "Socket opened");
			inStream = new ObjectInputStream(socket.getInputStream());
			writer = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
		} catch (SocketTimeoutException e) {
			log("Test runner process failed to start in a timely manner", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log("Error connecting to test runner process", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see #sendMessage(String)
	 */
	public synchronized void closeSocket() {
		try {
			if (writer != null) {
				if (!socket.isClosed()) {
					inStream.close();
					inStream = null;
					writer.close();
					writer = null;
					socket.close();
					socket = null;
					log(Level.CONFIG, "Socket closed");
				}
			}
		} catch (IOException e) {
			log(Level.INFO, "Tried to close socket, but was already closed");
		}
	}

	// Synchronized to prevent sending a message while the socket is being
	// closed
	public synchronized TestResults sendMessage(String testName) {
		try {
			writer.println(testName);
			return (TestResults) inStream.readObject();
		} catch (IOException e) {
			throw new TestRunAborted(testName, e);
		} catch (ClassNotFoundException e) {
			log("Error reading from socket", e);
			throw new MissingClassException("Error reading from socket", e);
		}

	}
}
