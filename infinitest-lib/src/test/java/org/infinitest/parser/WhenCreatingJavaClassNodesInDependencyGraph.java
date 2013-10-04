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

import static java.io.File.*;
import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.Collections;
import java.util.*;

import javassist.*;

import org.assertj.core.api.Assertions;
import org.infinitest.*;
import org.infinitest.util.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenCreatingJavaClassNodesInDependencyGraph {
  private JavaClassBuilder builder;
  private File newDir;

  @Before
  public void inContext() {
    ClasspathProvider classpath = fakeClasspath();
    builder = new JavaClassBuilder(classpath);
  }

  @After
  public void cleanup() {
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
  public void shouldReturnUnparsableClassIfClassCannotBeFound() {
    JavaClass javaClass = builder.createClass("foo.bar.com");

    assertThat(javaClass).isInstanceOf(UnparsableClass.class);
    assertThat(javaClass.getName()).isEqualTo("foo.bar.com");
    assertThat(javaClass.getImports()).isEmpty();
  }

  @Test
  public void shouldReturnUnparsableClassIfErrorOccursWhileParsing() {
    ClassParser parser = mock(ClassParser.class);
    when(parser.getClass("MyClassName")).thenThrow(new RuntimeException(new NotFoundException("")));

    builder = new JavaClassBuilder(parser);

    Assertions.assertThat(builder.createClass("MyClassName")).isInstanceOf(UnparsableClass.class);
  }

  @Test
  public void shouldLookForClassesInTargetDirectories() throws Exception {
    newDir = new File("tempClassDir");
    List<File> buildPaths = asList(newDir);
    ClasspathProvider classpath = new StandaloneClasspath(buildPaths, FakeEnvironments.systemClasspath() + pathSeparator + newDir.getAbsolutePath());

    String classname = "org.fakeco.Foobar";
    createClass(classname);

    builder = new JavaClassBuilder(classpath);
    JavaClass javaClass = builder.createClass(classname);
    assertEquals(classname, javaClass.getName());
    assertFalse(javaClass.isATest());
  }

  @Test
  public void shouldAlsoLookForClassesInClassDirectories() throws Exception {
    newDir = new File("tempClassDir");
    List<File> buildPaths = asList(newDir);
    ClasspathProvider classpath = new StandaloneClasspath(Collections.<File>emptyList(), buildPaths, FakeEnvironments.systemClasspath() + pathSeparator + newDir.getAbsolutePath());

    String classname = "org.fakeco.Foobar2";
    createClass(classname);

    builder = new JavaClassBuilder(classpath);
    JavaClass javaClass = builder.createClass(classname);
    assertEquals(classname, javaClass.getName());
    assertFalse(javaClass.isATest());
  }

  private void createClass(String classname) throws CannotCompileException, IOException {
    ClassPool pool = ClassPool.getDefault();
    CtClass foobarClass = pool.makeClass(classname);
    foobarClass.writeFile(newDir.getAbsolutePath());
  }

  @Test
  public void shouldFindDependenciesInSamePackage() {
    JavaClass javaClass = builder.createClass(FakeTree.class.getName());

    assertThat(javaClass.getImports()).contains(FakeDependency.class.getName());
  }
}
