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
package org.infinitest.config;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

public class FileBasedInfinitestConfigurationSource implements InfinitestConfigurationSource {

	private static final String INFINITEST_FILTERS_FILE_NAME = "infinitest.filters";
	private final File file;

	private FileBasedInfinitestConfigurationSource(File file) {
		this.file = file;
	}

	@Override
	public InfinitestConfiguration getConfiguration() {
		if (!file.exists()) {
			return InfinitestConfiguration.empty();
		}
		CharSource charSource = Files.asCharSource(file, Charsets.UTF_8);
		try {
			return new InfinitestConfigurationParser().parseFileContent(charSource);
		} catch (IOException e) {
			throw new RuntimeException("Error loading configuration", e);
		}
	}

	public static FileBasedInfinitestConfigurationSource createFromFile(File configFile) {
		return new FileBasedInfinitestConfigurationSource(configFile);
	}

	public static FileBasedInfinitestConfigurationSource createFromWorkingDirectory(File workingDirectory) {
		File configFile = new File(workingDirectory, INFINITEST_FILTERS_FILE_NAME);
		return new FileBasedInfinitestConfigurationSource(configFile);
	}

	public static InfinitestConfigurationSource createFromCurrentWorkingDirectory() {
		File workingDirectory = new File(".");
		return createFromWorkingDirectory(workingDirectory);
	}

}
