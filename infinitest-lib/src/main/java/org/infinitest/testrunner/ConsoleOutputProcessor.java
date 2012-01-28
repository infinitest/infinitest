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
