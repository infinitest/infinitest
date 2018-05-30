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

import static java.util.Arrays.asList;
import static java.util.logging.Level.INFO;
import static org.infinitest.util.InfinitestUtils.log;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.infinitest.ConsoleOutputListener.OutputType;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.testrunner.NativeRunner;
import org.infinitest.testrunner.OutputStreamHandler;
import org.infinitest.testrunner.TestRunnerProcess;

public class NativeConnectionFactory implements ProcessConnectionFactory {
	private final Class<? extends NativeRunner> runnerClass;

	public NativeConnectionFactory(Class<? extends NativeRunner> testRunnerClass) {
		runnerClass = testRunnerClass;
	}

	@Override
	public ProcessConnection getConnection(RuntimeEnvironment environment, OutputStreamHandler outputListener) throws IOException {
		TcpSocketProcessCommunicator communicator = createCommunicator();
				
		File classpathFile = environment.createClasspathFile();
		
		Process process = startProcess(communicator.createSocket(), environment, classpathFile);
		outputListener.processStream(process.getErrorStream(), OutputType.STDERR);
		outputListener.processStream(process.getInputStream(), OutputType.STDOUT);
		communicator.openSocket();
		return new NativeProcessConnection(communicator, process, classpathFile);
	}



	protected TcpSocketProcessCommunicator createCommunicator() {
		return new TcpSocketProcessCommunicator();
	}

	Process startProcess(int port, RuntimeEnvironment environment, File classPathFile) throws IOException {
		
		ProcessBuilder builder = buildProcess(port, environment, classPathFile);
		log(INFO, "Starting TestRunner with configuration:\n"+buildTestProcessConfigurationMessage(builder));
		try {
			return builder.start();
		} catch (IOException e) {
			String message = "Failed to start runner process with configuration\n" +buildTestProcessConfigurationMessage(builder);
			throw new IOException(message, e);
		}
	}

	ProcessBuilder buildProcess(int port, RuntimeEnvironment environment, File classPathFile) {
		// Could extract this to a class. Could then replace with:
		// http://wiki.eclipse.org/FAQ_How_do_I_launch_a_Java_program%3F
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(environment.getWorkingDirectory());

		List<String> arguments = environment.createProcessArguments(classPathFile);
		arguments.addAll(buildRunnerArgs(port));
		builder.command(arguments);

		builder.environment().putAll(environment.createProcessEnvironment());

		return builder;
	}

	
	
	private String buildTestProcessConfigurationMessage(ProcessBuilder builder) {
		String lineSeparator = System.getProperty("line.separator");

		StringBuilder message = new StringBuilder();
		message.append("  Directory: ").append(builder.directory().getAbsolutePath()).append(lineSeparator);
		message.append("  Environment:").append(lineSeparator);
		for (Map.Entry<String, String> e: builder.environment().entrySet()) {
			message.append("    ").append(e.getKey()).append("=").append(e.getValue()).append(lineSeparator);	
		}
		
		message.append("  Command: ").append(builder.command());

		return message.toString();
	}

	private Collection<String> buildRunnerArgs(int portNum) {
		return asList(
				TestRunnerProcess.class.getName(), 
				runnerClass.getName(), 
				String.valueOf(portNum));
	}
}
