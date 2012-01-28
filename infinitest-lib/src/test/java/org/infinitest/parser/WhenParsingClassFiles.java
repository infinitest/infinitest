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

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.infinitest.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenParsingClassFiles {
	private ClassParser parser;

	@Before
	public void inContext() {
		parser = new JavaAssistClassParser(fakeClasspath().getCompleteClasspath());
	}

	private JavaClass parseClass(Class<?> classToParse) {
		return parser.getClass(classToParse.getName());
	}

	@Test
	public void shouldLazilyCreateClassPool() {
		new JavaAssistClassParser("doesNotExist.jar");
	}

	@Test(expected = MissingClassException.class)
	public void shouldThrowMissingClassExceptionIfClasspathElementsCannotBeFound() {
		JavaAssistClassParser classParser = new JavaAssistClassParser("doesNotExist.jar");
		classParser.getClass("doesn't matter");
	}

	@Test
	public void shouldIncludeSystemClasspathInClasspool() {
		JavaClass stringClass = parseClass(String.class);
		assertEquals(String.class.getName(), stringClass.getName());
	}

	@Test
	public void shouldAddImportsFromAnnotations() {
		JavaClass clazz = parseClass(AnnotatedClass.class);
		assertEquals(AnnotatedClass.class.getName(), clazz.getName());
		Collection<String> imports = clazz.getImports();
		assertTrue(imports.contains(MethodAnnotation.class.getName()));
		assertTrue(imports.contains(ParameterAnnotation.class.getName()));
		assertTrue(imports.contains(ClassAnnotation.class.getName()));
	}

	@Test
	public void shouldDetectFieldAnnotations() {
		JavaClass javaClass = parseClass(FakeProduct.class);
		Collection<String> imports = javaClass.getImports();
		assertTrue(imports.contains(FieldAnnotation.class.getName()));
	}

	@Test
	public void shouldSkipMissingJarFilesWhenCreatingClassPool() {
		String classpath = fakeClasspath().getCompleteClasspath();
		classpath += File.pathSeparator + "notAJar.jar";
		parser = new JavaAssistClassParser(classpath);
		assertNotNull(parseClass(FakeProduct.class));
	}

	@Test
	public void shouldHandleMissingClassDirs() {
		parser = new JavaAssistClassParser("notADirYet");
	}
}
