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
package org.infinitest.parser;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.infinitest.changedetect.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class ClassFileIndexTest {
	private ClassFileIndex index;
	private ClassBuilder builder;

	@Before
	public void inContext() {
		builder = mock(ClassBuilder.class);
		index = new ClassFileIndex(builder);
	}

	@Test
	public void shouldClearClassBuilderAfterLookingForJavaFilesToReduceMemoryFootprint() {
		when(builder.loadClass(any(File.class))).thenReturn(new FakeJavaClass(""));

		index.findClasses(asList(getFileForClass(FakeProduct.class)));

		verify(builder).clear();
	}

	@Test
	public void shouldReplaceEntriesInTheIndex() {
		JavaClass secondClass = new FakeJavaClass("FakeProduct");
		when(builder.loadClass(any(File.class))).thenReturn(new FakeJavaClass("FakeProduct"));
		when(builder.loadClass(any(File.class))).thenReturn(secondClass);

		index.findClasses(asList(getFileForClass(FakeProduct.class)));
		index.findClasses(asList(getFileForClass(FakeProduct.class)));

		assertSame(secondClass, index.findJavaClass("FakeProduct"));
		verify(builder, times(2)).clear();
	}

	@Test
	public void shouldDisposeOfJavaClassesAfterAddingToIndex() {
		JavaClass mockClass = mock(JavaClass.class);
		when(mockClass.locatedInClassFile()).thenReturn(true);
		when(builder.createClass("FakeClass")).thenReturn(mockClass);

		index.findJavaClass("FakeClass");

		verify(mockClass).dispose();
	}

	@Test
	public void shouldIgnoreClassFilesThatCannotBeParsed() {
		ClassFileIndex index = new ClassFileIndex(fakeClasspath());
		assertEquals(Collections.emptySet(), index.findClasses(newArrayList(new File("notAClassFile"))));
	}

	public static void main(String[] args) throws IOException {
		FileChangeDetector detector = new FileChangeDetector();
		detector.setClasspathProvider(fakeClasspath());
		ArrayList<File> files = newArrayList(detector.findChangedFiles());
		List<ClassFileIndex> indexes = newArrayList();
		int totalClasses = 0;
		long start = System.currentTimeMillis();
		while (true) {
			ClassFileIndex index = new ClassFileIndex(fakeClasspath());
			index.findClasses(files);
			indexes.add(index);
			totalClasses += index.getIndexedClasses().size();
			System.out.println(totalClasses + "\t" + (System.currentTimeMillis() - start));
		}
	}
}
