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
package org.infinitest;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.infinitest.filter.*;
import org.infinitest.parser.*;
import org.junit.*;

public class WhenTheFilterFileChanges {
	private TestFilter list;
	private final String CLASS_NAME = "com.foo.Bar";

	@Test
	public void shouldUpdateTheFilterList() throws Exception {
		File file = File.createTempFile("infinitest", "shouldUpdateTheFilterList");
		list = new RegexFileFilter(file);
		assertFalse(list.match(CLASS_NAME));

		PrintWriter writer = new PrintWriter(file);
		writer.println(CLASS_NAME);
		writer.close();
		list.updateFilterList();
		assertTrue(list.match(CLASS_NAME));
	}

	@Test
	public void shouldRecognizeChangesBeforeLookingForTests() {
		TestFilter fakeFilterList = new RegexFileFilter() {
			@Override
			public void updateFilterList() {
				addFilter(CLASS_NAME);
			}
		};
		TestDetector detector = new ClassFileTestDetector(fakeFilterList);
		detector.setClasspathProvider(emptyClasspath());
		detector.findTestsToRun(Collections.<File> emptySet());
		assertTrue(fakeFilterList.match(CLASS_NAME));
	}
}
