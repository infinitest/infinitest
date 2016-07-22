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
package org.infinitest.filter;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.infinitest.parser.*;
import org.junit.*;
import org.junit.rules.*;
import org.testng.reporters.*;

import com.google.common.base.*;

public class RegexFileFilterTest {

	@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testFilterContentIsEmpty() throws Exception {

		File filterFile = createFilterFile();
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(false));
	}

	@Test
	public void testFilterContentContainsSimpleEntry() throws Exception {

		File filterFile = createFilterFile("SomeTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(true));
	}

	@Test
	public void testFilterContentContainsBlankLines() throws Exception {

		File filterFile = createFilterFile("", "SomeTest", "");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(true));
	}

	@Test
	public void testFilterContentContainsCommentedLines() throws Exception {

		File filterFile = createFilterFile("!SomeTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(false));
	}

	@Test
	public void testFilterContentContainsCommentedLines2() throws Exception {

		File filterFile = createFilterFile("#SomeTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(false));
	}

	@Test
	public void testFilterContentContainsSimpleEntryButDoesNotMatch() throws Exception {

		File filterFile = createFilterFile("SomeTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("OtherTest")), is(false));
	}

	@Test
	public void testFilterContentContainsSimpleExcludesEntry() throws Exception {

		File filterFile = createFilterFile("exclude=SomeTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(true));
	}

	@Test
	public void testFilterContentContainsSimpleIncludesEntry() throws Exception {

		File filterFile = createFilterFile("include=SomeTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(false));
		assertThat(filter.match(mockClass("OtherTest")), is(true));
	}

	@Test
	public void testFilterContentContainsIncludesExcludesEntries() throws Exception {

		File filterFile = createFilterFile("include=SomeTest", "exclude=.*Test");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("SomeTest")), is(true));
		assertThat(filter.match(mockClass("OtherTest")), is(true));
		assertThat(filter.match(mockClass("FooTest")), is(true));

	}

	@Test
	public void testFilterContentContainsComplexEntries() throws Exception {

		File filterFile = createFilterFile("include=com\\.mycompany\\.mypackage1\\.*", "include=com\\.mycompany\\.mypackage2\\.*", "exclude=.*IT", "exclude=.*SlowTest");
		RegexFileFilter filter = new RegexFileFilter(filterFile);

		assertThat(filter.match(mockClass("com.mycompany.mypackage1.SomeTest")), is(false));
		assertThat(filter.match(mockClass("com.mycompany.mypackage2.SomeTest")), is(false));
		assertThat(filter.match(mockClass("com.mycompany.mypackage3.SomeTest")), is(true));
		assertThat(filter.match(mockClass("com.mycompany.mypackage1.SomeIT")), is(true));
		assertThat(filter.match(mockClass("com.mycompany.mypackage2.OtherIT")), is(true));
		assertThat(filter.match(mockClass("com.mycompany.mypackage1.SlowTest")), is(true));
		assertThat(filter.match(mockClass("com.mycompany.mypackage2.OtherTest")), is(false));
		assertThat(filter.match(mockClass("OtherTest")), is(true));
		assertThat(filter.match(mockClass("SomeTest")), is(true));
	}

	private File createFilterFile(String... lines) throws Exception {
		File file = temporaryFolder.newFile();
		String content = Joiner.on("\n").join(lines);
		Files.writeFile(content, file);
		return file;
	}

	private JavaClass mockClass(String name) {
		JavaClass javaClass = mock(JavaClass.class);
		when(javaClass.getName()).thenReturn(name);
		return javaClass;
	}

}
