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

import static com.google.common.base.Charsets.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.io.Files.*;
import static org.infinitest.environment.FileCustomJvmArgumentReader.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.infinitest.environment.FileCustomJvmArgumentReader;
import org.junit.*;

public class FileCustomJvmArgumentReaderTest {
	private File tempDirectory;
	private File file;
	private FileCustomJvmArgumentReader reader;

	@Before
	public void setUp() throws IOException {
		tempDirectory = new File(System.getProperty("java.io.tmpdir"));
		assertTrue(tempDirectory.exists());
		assertTrue(tempDirectory.exists());
		file = new File(tempDirectory, FILE_NAME);
		file.createNewFile();
		assertTrue("Failed to create arguments file.", file.exists());

		reader = new FileCustomJvmArgumentReader(tempDirectory);
	}

	@After
	public void tearDown() {
		// assertTrue("Failed to delete arguments file.", file.delete());
		file.delete();
	}

	@Test
	public void shouldReturnEmptyListIfDirectoryDoesNotExists() {
		FileCustomJvmArgumentReader argReader = new FileCustomJvmArgumentReader(new File("fileThatDoesNotExist"));

		List<String> arguments = argReader.readCustomArguments();

		assertNotNull(arguments);
		assertTrue(arguments.isEmpty());
	}

	@Test
	public void shouldReturnEmptyListIfFileIsEmpty() {
		List<String> arguments = reader.readCustomArguments();

		assertNotNull(arguments);
		assertTrue(arguments.isEmpty());
	}

	@Test
	public void shouldReturnArgumentsAsListIfFileHasContents() throws IOException {
		String singleArgument = writeArguments("-DsomeArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(newArrayList(singleArgument), arguments);
	}

	@Test
	public void shouldReturnEachArgumentAsSeparateEntryInList() throws IOException {
		writeArguments("-DsomeArg=foo -DanotherArg=foo");

		List<String> arguments = reader.readCustomArguments();

		assertEquals(newArrayList("-DsomeArg=foo", "-DanotherArg=foo"), arguments);
	}

	private String writeArguments(String arguments) throws IOException, FileNotFoundException {
		String singleArgument = arguments;
		write(singleArgument, file, UTF_8);
		return singleArgument;
	}
}
