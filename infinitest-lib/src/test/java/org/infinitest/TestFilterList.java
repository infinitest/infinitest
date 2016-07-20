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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.infinitest.filter.*;
import org.infinitest.parser.*;
import org.junit.*;
import org.junit.rules.*;

public class TestFilterList {
	private final static String TEST_FILTER_LIST_REGEX = "org\\.infinitest\\.TestFilterList";

	@ClassRule public static TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testFilterList() throws IOException {
		TestFilter filter = new RegexFileFilter(file(TEST_FILTER_LIST_REGEX + "\n!foo.bar\n#foo.bar"));

		assertTrue(filter.match(javaClass(TestFilterList.class)));
	}

	@Test
	public void shouldIgnoreAllBlankLines() {
		TestFilter filter = new RegexFileFilter(file("\n\n"));

		assertFalse(filter.match(javaClass("MyClassName")));
	}

	@Test
	public void shouldNotInvertWhenKeywordIsPlacedAfterFirstFilter() throws Exception {

		TestFilter filter = new RegexFileFilter(file("MyOtherClassName\n!whitelist\nMyClassName\n"));

		assertTrue(filter.match(javaClass("MyClassName")));
		assertTrue(filter.match(javaClass("MyOtherClassName")));
	}

	@Test
	public void testJMockClasses() {
		TestFilter filter = new RegexFileFilter(file("org.jmock.test.acceptance.junit4.testdata.JUnit4TestWithNonPublicBeforeMethod"));

		assertTrue(filter.match(javaClass("org.jmock.test.acceptance.junit4.testdata.JUnit4TestWithNonPublicBeforeMethod")));
	}

	@Test
	public void testFiltering() {
		TestFilter filter = new RegexFileFilter(file("org\\.infinitest\\..*"));

		assertFalse(filter.match(javaClass(com.fakeco.fakeproduct.TestFakeProduct.class)));
		assertFalse(filter.match(javaClass(com.fakeco.fakeproduct.FakeProduct.class)));
		assertTrue(filter.match(javaClass(org.infinitest.changedetect.WhenLookingForClassFiles.class)));
	}

	@Test
	public void testSingleTest() {
		TestFilter filter = new RegexFileFilter(file(TEST_FILTER_LIST_REGEX));

		assertFalse(filter.match(javaClass(RegexFileFilter.class)));
		assertTrue("Single test should match regex", filter.match(javaClass(TestFilterList.class)));
	}

	@Test
	public void testLeadingWildcard() {
		TestFilter filter = new RegexFileFilter(file(".*TestFilterList"));

		assertTrue(filter.match(javaClass(TestFilterList.class)));
	}

	@Test
	public void testTrailingWildcard() {
		TestFilter filter = new RegexFileFilter(file("org\\..*"));

		assertTrue(filter.match(javaClass(TestFilterList.class)));
	}

	static File file(String content) {
		try {
			File file = temporaryFolder.newFile();
			new PrintWriter(file).append(content).close();
			return file;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	static JavaClass javaClass(Class<?> clazz) {
		return javaClass(clazz.getName());
	}

	static JavaClass javaClass(String name) {
		JavaClass javaClass = mock(JavaClass.class);
		when(javaClass.getName()).thenReturn(name);
		return javaClass;
	}
}
