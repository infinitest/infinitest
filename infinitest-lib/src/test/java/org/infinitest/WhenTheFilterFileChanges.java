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
package org.infinitest;

import static org.infinitest.environment.FakeEnvironments.emptyClasspath;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.filter.RegexFileFilter;
import org.infinitest.filter.TestFilter;
import org.infinitest.parser.ClassFileTestDetector;
import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

public class WhenTheFilterFileChanges {
	@Test
	public void shouldUpdateTheFilterList() throws IOException {
		File file = File.createTempFile("infinitest", "shouldUpdateTheFilterList");
		file.deleteOnExit();

		FileBasedInfinitestConfigurationSource configSource = FileBasedInfinitestConfigurationSource.createFromFile(file);
		TestFilter list = new RegexFileFilter(configSource);
		assertFalse(list.match(javaClass("com.foo.Bar")));

		Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND).write("exclude com.foo.Bar");
		
		list.updateFilterList();
		assertTrue(list.match(javaClass("com.foo.Bar")));
	}

	@Test
	public void shouldRecognizeChangesBeforeLookingForTests() {
		TestFilter testFilter = mock(TestFilter.class);
		TestDetector detector = new ClassFileTestDetector(testFilter);

		detector.setClasspathProvider(emptyClasspath());
		detector.findTestsToRun(Collections.<File> emptySet());

		verify(testFilter).updateFilterList();
	}

	static JavaClass javaClass(String name) {
		JavaClass javaClass = mock(JavaClass.class);
		when(javaClass.getName()).thenReturn(name);
		return javaClass;
	}
}
