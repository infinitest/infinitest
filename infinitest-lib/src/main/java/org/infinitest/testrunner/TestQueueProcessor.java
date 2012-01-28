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
