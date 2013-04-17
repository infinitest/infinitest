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

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;

import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.*;
import org.infinitest.testrunner.process.*;
import org.infinitest.testrunner.queue.*;

class TestQueueProcessor implements QueueProcessor {
	private final ProcessConnectionFactory factory;
	private final RunnerEventSupport eventSupport;

	private final ProcessConnection currentConnection;

	public TestQueueProcessor(RunnerEventSupport eventSupport, ProcessConnectionFactory factory, RuntimeEnvironment environment) throws IOException {
		this.eventSupport = eventSupport;
		this.factory = factory;
		currentConnection = establishConnection(environment);
	}

	public void process(String testName) {
		getEventSupport().fireStartingEvent(testName);
		TestResults results = currentConnection.runTest(testName);
		getEventSupport().fireTestCaseComplete(testName, results);
	}

	public void close() {
		getEventSupport().fireTestRunComplete();
		currentConnection.close();
	}

	private ProcessConnection establishConnection(RuntimeEnvironment environment) throws IOException {
		return factory.getConnection(environment, new OutputStreamHandler() {
			public void processStream(InputStream stream, OutputType type) {
				new Thread(new ConsoleOutputProcessor(stream, type, getEventSupport())).start();
			}
		});
	}

	private RunnerEventSupport getEventSupport() {
		return eventSupport;
	}

	public void cleanup() {
		if (!currentConnection.abort()) {
			log(WARNING, "Failed to clean up after terminated test run");
		} else {
			log("Test runner process terminated");
		}
	}
}
