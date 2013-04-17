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
package org.infinitest.util;

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import org.infinitest.*;
import org.junit.*;

import com.google.common.collect.*;

public class WhenSearchingForEntriesOnTheClasspath {
	private String systemClasspath;

	@Before
	public void inContext() {
		systemClasspath = systemClasspath();
	}

	@Test
	public void shouldFindJarsThatContainAClass() {
		String entry = findClasspathEntryFor(systemClasspath, Iterables.class);
		assertThat(entry, containsString(".jar"));
		assertThat(entry, containsString("google"));
	}

	@Test
	public void shouldFindClassDirectoriesThatContainAClass() {
		String entry = findClasspathEntryFor(systemClasspath, InfinitestCore.class);
		assertThat(entry, not(containsString(".jar")));
		assertThat(entry, containsString("target/classes"));
	}
}
