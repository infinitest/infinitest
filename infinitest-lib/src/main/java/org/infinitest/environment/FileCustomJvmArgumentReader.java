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

import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

public class FileCustomJvmArgumentReader implements CustomJvmArgumentsReader {

	private static final String SPLIT_ON_LINE_PREFIX = "#SplitOn=";

	private enum SplitOn {
		LINES_ONLY, SPACES
	}

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
			return parseLines(lines);
		} catch (IOException e) {
			return emptyList();
		}
	}

	private List<String> parseLines(List<String> lines) {
		if (lines.isEmpty()) {
			return emptyList();
		}
		SplitOn splitOn;
		List<String> argumentLines;
		if (isSplitOnConfigurationLine(lines.get(0))) {
			splitOn = readSplitOn(lines.get(0));
			argumentLines = lines.subList(1, lines.size());
		} else {
			// No split one configuration use SPACES by default
			splitOn = SplitOn.SPACES;
			argumentLines = lines;
		}
		return buildArgumentList(argumentLines, splitOn);
	}

	private boolean isSplitOnConfigurationLine(String line) {
		return removeWhitespaces(line).startsWith(SPLIT_ON_LINE_PREFIX);
	}

	private String removeWhitespaces(String line) {
		return line.replaceAll("\\s", "");
	}

	private SplitOn readSplitOn(String line) {
		String splitOnTextValue = removeWhitespaces(line).substring(SPLIT_ON_LINE_PREFIX.length());
		return SplitOn.valueOf(splitOnTextValue);
	}

	private List<String> buildArgumentList(List<String> lines, SplitOn splitOn) {
		if (splitOn == SplitOn.LINES_ONLY) {
				return lines;
		} else {
			return lines.stream().flatMap(line -> parseArgumentLineOnSpace(line).stream()).collect(Collectors.toList());			
		}
	}

	private List<String> parseArgumentLineOnSpace(String line) {
		// Split arguments on space unless
		return Splitter.onPattern("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)").splitToList(line);
	}
}
