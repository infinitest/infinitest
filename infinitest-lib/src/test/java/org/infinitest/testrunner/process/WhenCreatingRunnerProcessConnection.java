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

import static com.google.common.collect.Iterables.addAll;
import static org.infinitest.testrunner.TestEvent.TestState.METHOD_FAILURE;
import static org.infinitest.util.FakeEnvironments.fakeEnvironment;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.infinitest.RuntimeEnvironment;
import org.infinitest.testrunner.CrashingTestRunner;
import org.infinitest.testrunner.FakeRunner;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.testrunner.TestResults;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class WhenCreatingRunnerProcessConnection {
	private NativeConnectionFactory factory;

	@Before
	public void setUp() {
		factory = new NativeConnectionFactory(FakeRunner.class);
	}

	@Test
	public void canCommunicateWithProcess() throws Exception {
		assertEquals("Hello World", sendMessageWithServerSocket("Hello World").get(0).getTestName());
	}

	@Test
	public void shouldPassMultipleArguments() throws Exception {
		List<TestEvent> events = sendMessageWithServerSocket("Hello", "World");
		assertEquals("Hello", events.get(0).getTestName());
		assertEquals(2, events.size());
		assertEquals("World", events.get(1).getTestName());
	}

	private List<TestEvent> sendMessageWithServerSocket(String input) throws UnknownHostException, IOException, ClassNotFoundException {
		return sendMessageWithServerSocket(new String[] { input });
	}

	private List<TestEvent> sendMessageWithServerSocket(String... messages) throws UnknownHostException, IOException, ClassNotFoundException {
		ServerSocket serverSocket = new ServerSocket(0);
		try {
			
			RuntimeEnvironment fakeEnvironment = fakeEnvironment();
			File file = fakeEnvironment.createClasspathFile();
			factory.startProcess(serverSocket.getLocalPort(), fakeEnvironment, file);
			serverSocket.setSoTimeout(2500);
			Socket socket = serverSocket.accept();
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			List<TestEvent> results = Lists.newArrayList();
			TestResults result = null;
			int i = 0;
			do {
				outStream.writeObject(messages[i++]);
				result = (TestResults) inStream.readObject();
				if (result != null) {
					addAll(results, result);
				}
			} while (i < messages.length);
			outStream.writeObject(null);
			inStream.close();
			outStream.close();
			socket.close();
			return results;
		} finally {
			serverSocket.close();
		}
	}

	@Test
	public void shouldGracefullyHandleErrors() throws Exception {
		factory = new NativeConnectionFactory(CrashingTestRunner.class);
		TestEvent testEvent = sendMessageWithServerSocket("Hello").get(0);
		assertEquals(METHOD_FAILURE, testEvent.getType());
		assertEquals("StubErrorTestRunner is Broken", testEvent.getMessage());
	}
}
