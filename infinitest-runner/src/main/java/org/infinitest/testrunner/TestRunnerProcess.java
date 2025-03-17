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

import static org.infinitest.testrunner.TestEvent.methodFailed;
import static org.infinitest.testrunner.TestEvent.testInitializationFailed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.infinitest.TestClassInitializationException;
import org.infinitest.config.FileBasedInfinitestConfigurationSource;

// RISK This class is only tested by running it, which is slow and throws off coverage
public class TestRunnerProcess {
	public static final String TEST_RUN_ERROR = "Error occurred during test run";
	
	private NativeRunner runner;

	protected TestRunnerProcess(String runnerClass, File filterFile) {
		createRunner(runnerClass, filterFile);
	}

	private static void checkForJUnit4() {
		try {
			Class.forName("org.junit.runner.notification.RunListener");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Infinitest depends on JUnit 4. Please add JUnit 4 to your project classpath.");
		}
	}

	protected void createRunner(String runnerClassName, File filterFile) {
		runner = instantiateTestRunner(runnerClassName);
		
		if (filterFile != null && filterFile.exists()) {
			runner.setTestConfigurationSource(FileBasedInfinitestConfigurationSource.createFromFile(filterFile));
		}
	}

	protected NativeRunner instantiateTestRunner(String runnerClassName) {
		try {
			Class<?> runnerClass = Class.forName(runnerClassName);
			return (NativeRunner) runnerClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private TestResults runTest(String testName) {
		return runner.runTest(testName);
	}

	public static void main(String[] args) {
		try {
			checkForJUnit4();			
			checkArgsCount(args);
			
			String runnerClass = args[0];
			File filterFile = getFilterFile(args);
			TestRunnerProcess process = new TestRunnerProcess(runnerClass, filterFile);
			int portNum = Integer.parseInt(args[1]);
			Socket clientSocket = new Socket("127.0.0.1", portNum);
			// DEBT Extract this to a reader class
			ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

			String testName;
			do {
				testName = inputStream.readLine();

				if (testName != null) {
					writeTestResultToOutputStream(process, outputStream, testName);
				}

			} while (testName != null);

			outputStream.close();
			clientSocket.close();
		}
		// CHECKSTYLE:OFF
		catch (Throwable e)
		// CHECKSTYLE:ON
		{
			System.out.println(TEST_RUN_ERROR);
			e.printStackTrace();
		} finally {
			System.exit(0);
		}

	}

	public static void checkArgsCount(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException("runner expects three parameters: runnerClass, port and filterFile");
		}
	}

	public static File getFilterFile(String[] args) {
		if (!"null".equals(args[2])) {
			return new File(args[2]);
		} else {
			return null;
		}
	}

	public static void writeTestResultToOutputStream(TestRunnerProcess process, ObjectOutputStream outputStream, String testName) throws IOException {
		TestResults results;
		try {
			results = process.runTest(testName);
		} catch (TestClassInitializationException e) {
			results = new TestResults(testInitializationFailed(testName, "", e));
		} catch (Throwable e) {
			results = new TestResults(methodFailed(testName, "", e));
		}
		
		outputStream.writeObject(results);
	}
}
