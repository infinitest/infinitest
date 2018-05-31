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
package org.infinitest.environment;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;

import java.io.*;
import java.util.*;

import com.google.common.base.*;
import com.google.common.io.*;

public class FileCustomJvmArgumentReader implements CustomJvmArgumentsReader {
	public static final String FILE_NAME = "infinitest.args";

	private final File parentDirectory;

	public FileCustomJvmArgumentReader(File parentDirectory) {
		this.parentDirectory = parentDirectory;
	}

	@Override
	public List<String> readCustomArguments() {
		File file = new File(parentDirectory, FILE_NAME);
		if (!file.exists()) {
			return emptyList();
		}

		try {
			List<String> lines = Files.readLines(file, Charsets.UTF_8);
			return buildArgumentList(lines);
		} catch (IOException e) {
			return emptyList();
		}
	}

	private List<String> buildArgumentList(List<String> lines) {
		List<String> arguments = newArrayList();
		for (String line : lines) {
			for (String arg : line.split(" ")) {
				arguments.add(arg);
			}
		}

		return arguments;
	}
}
