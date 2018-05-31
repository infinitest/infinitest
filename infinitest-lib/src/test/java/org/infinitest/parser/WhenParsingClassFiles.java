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
package org.infinitest.parser;

import static org.assertj.core.api.Assertions.*;
import static org.infinitest.environment.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.*;

import org.infinitest.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenParsingClassFiles {
	private JavaAssistClassParser parser;

	@Before
	public void inContext() {
		parser = new JavaAssistClassParser(fakeClasspath().getRunnerFullClassPath());
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

		String[] imports = clazz.getImports();

		assertThat(imports).contains(MethodAnnotation.class.getName(), ParameterAnnotation.class.getName(), ClassAnnotation.class.getName());
	}

	@Test
	public void shouldDetectFieldAnnotations() {
		JavaClass javaClass = parseClass(FakeProduct.class);

		String[] imports = javaClass.getImports();

		assertThat(imports).contains(FieldAnnotation.class.getName());
	}

	@Test
	public void shouldSkipMissingJarFilesWhenCreatingClassPool() {
		String classpath = fakeClasspath().getRunnerFullClassPath();
		classpath += File.pathSeparator + "notAJar.jar";
		parser = new JavaAssistClassParser(classpath);
		assertNotNull(parseClass(FakeProduct.class));
	}

	@Test
	public void shouldHandleMissingClassDirs() {
		parser = new JavaAssistClassParser("notADirYet");
	}
}
