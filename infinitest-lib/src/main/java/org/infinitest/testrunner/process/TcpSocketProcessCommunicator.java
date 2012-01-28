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
package org.infinitest.testrunner.process;

import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import org.infinitest.*;
import org.infinitest.testrunner.*;

public class TcpSocketProcessCommunicator {
	private ServerSocket serverSocket;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
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
			outStream = new ObjectOutputStream(socket.getOutputStream());
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
			if (outStream != null) {
				outStream.writeObject(null);
				if (!socket.isClosed()) {
					inStream.close();
					inStream = null;
					outStream.close();
					outStream = null;
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
			outStream.writeObject(testName);
			return (TestResults) inStream.readObject();
		} catch (IOException e) {
			throw new TestRunAborted(testName, e);
		} catch (ClassNotFoundException e) {
			log("Error reading from socket", e);
			throw new MissingClassException("Error reading from socket", e);
		}

	}
}
