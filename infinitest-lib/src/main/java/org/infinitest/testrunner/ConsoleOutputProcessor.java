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

import static org.infinitest.util.InfinitestUtils.*;

import java.io.*;

import org.infinitest.ConsoleOutputListener.OutputType;

public class ConsoleOutputProcessor implements Runnable {
	private final RunnerEventSupport eventSupport;
	private final InputStream outputSource;
	private final OutputType outputType;

	public ConsoleOutputProcessor(InputStream stream, OutputType outputType, RunnerEventSupport eventSupport) {
		outputSource = stream;
		this.outputType = outputType;
		this.eventSupport = eventSupport;
	}

	public void run() {
		try {
			int bytesRead;
			byte[] buffer = new byte[1024 * 10];
			while ((bytesRead = outputSource.read(buffer)) != -1) {
				eventSupport.fireConsoleUpdateEvent(new String(buffer, 0, bytesRead), outputType);
			}
			outputSource.close();
		} catch (IOException e) {
			log("Error while reading console output from the test runner process", e);
			throw new RuntimeException(e);
		}
	}
}
