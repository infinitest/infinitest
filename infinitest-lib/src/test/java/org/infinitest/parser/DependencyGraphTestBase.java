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

import static org.infinitest.environment.FakeEnvironments.fakeClasspath;
import static org.infinitest.util.InfinitestTestUtils.getFileForClass;
import static org.infinitest.util.InfinitestUtils.setify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.infinitest.filter.TestFilter;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.jupiter.api.BeforeEach;

abstract class DependencyGraphTestBase {
  private final FilterStub filter = new FilterStub();
  private ClassFileTestDetector testDetector;

  @BeforeEach
  public final void setUp() {
    testDetector = new ClassFileTestDetector(filter);
    testDetector.setClasspathProvider(fakeClasspath());
    testDetector.findTestsToRun(Collections.<File>emptySet());
  }

  protected Set<JavaClass> findTestsForChangedFiles(Class<?>... classes) {
    Set<File> fileSet = new HashSet<File>();
    for (Class<?> clazz : classes) {
      fileSet.add(getFileForClass(clazz));
    }
    return getGraph().findTestsToRun(fileSet);
  }

  protected void addToDependencyGraph(Class<?>... classes) {
    findTestsForChangedFiles(classes);
  }

  protected void assertClassRecognizedAsTest(Class<?> testClass) {
    Set<File> fileSet = setify(InfinitestTestUtils.getFileForClass(testClass));
    Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
    assertEquals(1, testsToRun.size(), testClass.getSimpleName() + " should have been recognized as a test");
    JavaClass testToRun = testsToRun.iterator().next();
    assertEquals(testClass.getName(), testToRun.getName());
    assertTrue(testToRun.isATest());
  }

  protected void verifyDependency(Class<?> changedFile, Class<?> expectedTest) {
    Set<JavaClass> testsToRun = findTestsForChangedFiles(changedFile);
    assertTrue(testsToRun.contains(getGraph().findJavaClass(expectedTest.getName())), "Changing " + changedFile + " did not cause " + expectedTest + " to be run");
  }

  protected ClassFileTestDetector getGraph() {
    return testDetector;
  }

  protected void addFilter(String className) {
    filter.addClass(className);
  }

  static class FilterStub implements TestFilter {
    private final Set<String> classesToFilter = new HashSet<String>();

    @Override
    public boolean match(JavaClass javaClass) {
      return classesToFilter.contains(javaClass.getName());
    }

    @Override
    public void updateFilterList() {
      // nothing to do here
    }

    void addClass(String className) {
      classesToFilter.add(className);
    }
  }
}
