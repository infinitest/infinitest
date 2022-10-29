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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.write;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.infinitest.environment.FileCustomJvmArgumentReader.FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class FileCustomJvmArgumentReaderTest {
	private File tempDirectory;
	private File file;
	private FileCustomJvmArgumentReader reader;

	@BeforeEach
	void setUp() throws IOException {
		tempDirectory = new File(System.getProperty("java.io.tmpdir"));
		assertTrue(tempDirectory.exists());
		assertTrue(tempDirectory.exists());
		file = new File(tempDirectory, FILE_NAME);
		file.createNewFile();
		assertTrue(file.exists(), "Failed to create arguments file.");

		reader = new FileCustomJvmArgumentReader(tempDirectory);
	}

	@AfterEach
	void tearDown() {
		// assertTrue("Failed to delete arguments file.", file.delete());
		file.delete();
	}

	@Test
	void shouldReturnEmptyListIfDirectoryDoesNotExists() {
		FileCustomJvmArgumentReader argReader = new FileCustomJvmArgumentReader(new File("fileThatDoesNotExist"));

		List<String> arguments = argReader.readCustomArguments();

		assertNotNull(arguments);
		assertTrue(arguments.isEmpty());
	}

	@Test
	void shouldReturnEmptyListIfFileIsEmpty() {
		List<String> arguments = reader.readCustomArguments();

		assertNotNull(arguments);
		assertTrue(arguments.isEmpty());
	}

	@Test
	void shouldReturnArgumentsAsListIfFileHasContents() throws IOException {
		String singleArgument = writeArguments("-DsomeArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(singletonList(singleArgument), arguments);
	}
	
	@Test
	void withoutSplitOnConfigShouldSplitArgumentsOnSpaces() throws IOException {
		writeArguments("-DsomeArg=foo -DanotherArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(asList("-DsomeArg=foo", "-DanotherArg=foo"), arguments);
	}
	
	@Test
	void withoutSplitOnConfigShouldNotSplitArgumentsOnSpacesBetweenQuotes() throws IOException {
		writeArguments("-DsomeArg=\"hello world\" -DanotherArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(asList("-DsomeArg=\"hello world\"", "-DanotherArg=foo"), arguments);
	}
	
	@Test
	void withoutSplitOnConfigShouldSplitArgumentsOnLines() throws IOException {
		writeArguments("-DsomeArg=foo\n-DanotherArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(asList("-DsomeArg=foo", "-DanotherArg=foo"), arguments);
	}

	
	@Test
	void withSplitOnLinesOnlyConfigShouldSplitArgumentsOnLines() throws IOException {
		writeArguments("# SplitOn=LINES_ONLY\n-DsomeArg=foo\n-DanotherArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(asList("-DsomeArg=foo", "-DanotherArg=foo"), arguments);
	}
	
	@Test
	void withSplitOnLinesOnlyConfigShouldNotSplitArgumentsOnSpace() throws IOException {
		writeArguments("# SplitOn=LINES_ONLY\n-DsomeArg=foo not another Arg");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(asList("-DsomeArg=foo not another Arg"), arguments);
	}



	private String writeArguments(String arguments) throws IOException, FileNotFoundException {
		String singleArgument = arguments;
		write(singleArgument, file, UTF_8);
		return singleArgument;
	}
}
