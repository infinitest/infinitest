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

import static java.io.File.pathSeparator;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.fakeClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.infinitest.StandaloneClasspath;
import org.infinitest.environment.ClasspathProvider;
import org.infinitest.environment.FakeEnvironments;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.FakeDependency;
import com.fakeco.fakeproduct.FakeTree;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

class WhenCreatingJavaClassNodesInDependencyGraph {
	private JavaClassBuilder builder;
	private File newDir;

	@BeforeEach
	void inContext() {
		ClasspathProvider classpath = fakeClasspath();
		builder = new JavaClassBuilder(classpath);
	}

	@AfterEach
	void cleanup() {
		if (newDir != null) {
			delete(newDir);
		}
	}

	private static void delete(File directory) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				delete(file);
			} else {
				assertTrue(file.delete());
			}
		}
		assertTrue(directory.delete());
	}

	@Test
	void shouldReturnUnparsableClassIfClassCannotBeFound() {
		JavaClass javaClass = builder.getClass("foo.bar.com");

		assertThat(javaClass).isInstanceOf(UnparsableClass.class);
		assertThat(javaClass.getName()).isEqualTo("foo.bar.com");
		assertThat(javaClass.getImports()).isEmpty();
	}

	@Test
	void shouldReturnUnparsableClassIfErrorOccursWhileParsing() {
		JavaAssistClassParser parser = mock(JavaAssistClassParser.class);
		when(parser.getClass("MyClassName")).thenThrow(new RuntimeException(new NotFoundException("")));

		builder = new JavaClassBuilder(parser);

		Assertions.assertThat(builder.getClass("MyClassName")).isInstanceOf(UnparsableClass.class);
	}

	@Test
	void shouldLookForClassesInTargetDirectories() throws Exception {
		newDir = new File("tempClassDir");
		List<File> buildPaths = asList(newDir);
		ClasspathProvider classpath = new StandaloneClasspath(buildPaths, FakeEnvironments.systemClasspath() + pathSeparator + newDir.getAbsolutePath());

		String classname = "org.fakeco.Foobar";
		createClass(classname);

		builder = new JavaClassBuilder(classpath);
		JavaClass javaClass = builder.getClass(classname);
		assertEquals(classname, javaClass.getName());
		assertFalse(javaClass.isATest());
	}

	@Test
	void shouldAlsoLookForClassesInClassDirectories() throws Exception {
		newDir = new File("tempClassDir");
		List<File> buildPaths = asList(newDir);
		ClasspathProvider classpath = new StandaloneClasspath(Collections.<File> emptyList(), buildPaths, FakeEnvironments.systemClasspath() + pathSeparator + newDir.getAbsolutePath());

		String classname = "org.fakeco.Foobar2";
		createClass(classname);

		builder = new JavaClassBuilder(classpath);
		JavaClass javaClass = builder.getClass(classname);
		assertEquals(classname, javaClass.getName());
		assertFalse(javaClass.isATest());
	}

	private void createClass(String classname) throws CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass foobarClass = pool.makeClass(classname);
		foobarClass.writeFile(newDir.getAbsolutePath());
	}

	@Test
	void shouldFindDependenciesInSamePackage() {
		JavaClass javaClass = builder.getClass(FakeTree.class.getName());

		assertThat(javaClass.getImports()).contains(FakeDependency.class.getName());
	}
}
