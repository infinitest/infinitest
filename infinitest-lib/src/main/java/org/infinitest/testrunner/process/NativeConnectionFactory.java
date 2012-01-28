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

import static java.util.Arrays.*;
import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;
import java.util.*;

import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.*;
import org.infinitest.testrunner.*;

public class NativeConnectionFactory implements ProcessConnectionFactory {
	private final Class<? extends NativeRunner> runnerClass;

	public NativeConnectionFactory(Class<? extends NativeRunner> testRunnerClass) {
		runnerClass = testRunnerClass;
	}

	public ProcessConnection getConnection(RuntimeEnvironment environment, OutputStreamHandler outputListener) throws IOException {
		TcpSocketProcessCommunicator communicator = createCommunicator();
		Process process = startProcess(communicator.createSocket(), environment);
		outputListener.processStream(process.getErrorStream(), OutputType.STDERR);
		outputListener.processStream(process.getInputStream(), OutputType.STDOUT);
		communicator.openSocket();
		return new NativeProcessConnection(communicator, process);
	}

	protected TcpSocketProcessCommunicator createCommunicator() {
		return new TcpSocketProcessCommunicator();
	}

	Process startProcess(int port, RuntimeEnvironment environment) throws IOException {
		ProcessBuilder builder = buildProcess(port, environment);
		return builder.start();
	}

	ProcessBuilder buildProcess(int port, RuntimeEnvironment environment) {
		// Could extract this to a class. Could then replace with:
		// http://wiki.eclipse.org/FAQ_How_do_I_launch_a_Java_program%3F
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(environment.getWorkingDirectory());

		List<String> arguments = environment.createProcessArguments();
		arguments.addAll(buildRunnerArgs(port));
		builder.command(arguments);

		builder.environment().putAll(environment.createProcessEnvironment());

		logProcessEnvironment(builder);
		return builder;
	}

	private void logProcessEnvironment(ProcessBuilder builder) {
		String lineSeparator = System.getProperty("line.separator");

		StringBuilder message = new StringBuilder();
		message.append("Launching test runner process with the following configuration:").append(lineSeparator);
		message.append("Directory: ").append(builder.directory().getAbsolutePath()).append(lineSeparator).append("Environment: ").append(builder.environment()).append(lineSeparator).append("Command: ").append(builder.command());

		log(INFO, message.toString());
	}

	private Collection<String> buildRunnerArgs(int portNum) {
		return asList(TestRunnerProcess.class.getName(), runnerClass.getName(), String.valueOf(portNum));
	}
}
