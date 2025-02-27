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

import static org.assertj.core.api.Assertions.assertThat;
import static org.infinitest.environment.FakeEnvironments.systemClasspath;
import static org.infinitest.util.InfinitestTestUtils.getFileForClass;
import static org.infinitest.util.InfinitestUtils.setify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Set;

import org.infinitest.environment.FakeEnvironments;
import org.infinitest.util.InfinitestTestUtils;
import org.junit.jupiter.api.Test;

import com.fakeco.fakeproduct.ANewClass;
import com.fakeco.fakeproduct.ClassAnnotation;
import com.fakeco.fakeproduct.FakeDependency;
import com.fakeco.fakeproduct.FakeEnum;
import com.fakeco.fakeproduct.FakeProduct;
import com.fakeco.fakeproduct.FakeTree;
import com.fakeco.fakeproduct.FieldAnnotation;
import com.fakeco.fakeproduct.JUnit4TestThatInherits;
import com.fakeco.fakeproduct.MethodAnnotation;
import com.fakeco.fakeproduct.TestFakeProduct;
import com.fakeco.fakeproduct.TestFakeTree;
import com.fakeco.fakeproduct.TestJUnit4TestCase;
import com.fakeco.fakeproduct.TestJunit3TestCase;
import com.fakeco.fakeproduct.TestThatInherits;
import com.fakeco.fakeproduct.id.FakeId;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import junit.framework.TestCase;

class WhenLookingForDependenciesBetweenClasses extends DependencyGraphTestBase {
  @Test
  void shouldFindDependenciesCreatedByAnnotations() {
    findTestsForChangedFiles(FakeProduct.class, TestFakeProduct.class);
    verifyDependency(MethodAnnotation.class, TestFakeProduct.class);
    verifyDependency(ClassAnnotation.class, TestFakeProduct.class);
    verifyDependency(FieldAnnotation.class, TestFakeProduct.class);
  }

  @Test
  void shouldFindDependenciesCreatedByEnums() {
    addToDependencyGraph(FakeProduct.class, TestFakeProduct.class);
    verifyDependency(FakeEnum.class, TestFakeProduct.class);
  }

  @Test
  void shouldFindDependenciesThatRouteThroughLibraries() {
    addToDependencyGraph(FakeId.class, FakeProduct.class, TestFakeProduct.class);
    verifyDependency(FakeId.class, TestFakeProduct.class);
  }

  @Test
  void canClearIndex() {
    File testFakeProductFile = InfinitestTestUtils.getFileForClass(TestFakeProduct.class);
    Set<File> fileSet = setify(testFakeProductFile);
    fileSet.add(InfinitestTestUtils.getFileForClass(FakeProduct.class));
    getGraph().findTestsToRun(fileSet);
    getGraph().clear();
    fileSet.remove(testFakeProductFile);
    assertEquals(1, fileSet.size());

    Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
    assertThat(testsToRun).as("Because FakeProduct is the only class in the graph, no tests should be returned").isEmpty();
  }

  @Test
  void shouldRunTestsForTransitiveDependencies() {
    addToDependencyGraph(FakeTree.class, FakeDependency.class, TestFakeTree.class);
    verifyDependency(FakeTree.class, TestFakeTree.class);
    verifyDependency(FakeDependency.class, TestFakeTree.class);
  }

  @Test
  void canFilterTests() {
    Set<File> fileSet = setify(getFileForClass(TestFakeProduct.class));
    Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
    assertThat(testsToRun).as("TestFakeProduct should have been found").hasSize(1);

    addFilter(TestFakeProduct.class.getName());
    testsToRun = getGraph().findTestsToRun(fileSet);
    assertThat(testsToRun).as("TestFakeProduct should have been filtered").isEmpty();
  }

  @Test
  void shouldFindJUnit4Tests() {
    assertClassRecognizedAsTest(TestJUnit4TestCase.class);
  }

  @Test
  void shouldFindJUnit4TestsThatInheritAllTheirTestMethods() {
    assertClassRecognizedAsTest(JUnit4TestThatInherits.class);
  }

  @Test
  void shouldFindNewlyCreatedClasses() throws Exception {
    ClassPool pool = ClassPool.getDefault();
    pool.appendPathList(systemClasspath());
    CtClass cc = pool.makeClass("org.infinitest.parser.ANewTest");
    cc.setSuperclass(pool.get(TestCase.class.getName()));
    cc.addMethod(CtMethod.make("void testSomething(){}", cc));
    cc.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
    Class<?> testClass = cc.toClass(getClass());
    InfinitestTestUtils.getFileForClass(testClass).deleteOnExit();
    assertClassRecognizedAsTest(testClass);
  }

  @Test
  void testJUnit3Tests() {
    assertClassRecognizedAsTest(TestJunit3TestCase.class);
  }

  @Test
  void testClassesOutsideCollection() {
    assertFalse(getGraph().findJavaClass(Object.class.getName()).locatedInClassFile());
    assertTrue(getGraph().findJavaClass(FakeTree.class.getName()).locatedInClassFile());
    assertFalse(getGraph().isIndexed(Object.class), "The dependency graph should not index class files outside the target directory");
  }

  @Test
  void testEquality() {
    JavaClass class1 = new FakeJavaClass("com.fakeco.fakeproduct.FakeProduct$StaticInnerClass");
    JavaClass class2 = new FakeJavaClass("com.fakeco.fakeproduct.FakeProduct");
    JavaClass class3 = new FakeJavaClass("com.fakeco.fakeproduct.FakeProduct$StaticInnerClass");
    assertNotEquals(class1, class2);
    assertEquals(class1, class3);
  }

  @Test
  void shouldDetectDirectDependencies() {
    addToDependencyGraph(FakeProduct.class, TestFakeProduct.class);
    verifyDependency(FakeProduct.class, TestFakeProduct.class);
  }

  @Test
  void testInheritFromFilteredTest() {
    addFilter(TestFakeProduct.class.getName());
    Set<JavaClass> tests = getGraph().findTestsToRun(setify(InfinitestTestUtils.getFileForClass(TestThatInherits.class)));
    assertThat(tests)
    .as("Filtered test found").doesNotContain(new FakeJavaClass(TestFakeProduct.class.getName()))
    .as("Required test not found").contains(new FakeJavaClass(TestThatInherits.class.getName()));
  }

  @Test
  void testThreeWayIntraPackageDependency() {
    Class<FakeProduct> changedFile = FakeProduct.class;
    Class<TestFakeTree> expectedTest = TestFakeTree.class;
    addToDependencyGraph(changedFile, FakeTree.class, expectedTest);
    verifyDependency(changedFile, expectedTest);
  }

  @Test
  void shouldDetectNewClass() {
    Set<JavaClass> classes = findTestsForChangedFiles(FakeProduct.class);
    assertTrue(classes.isEmpty());

    classes = findTestsForChangedFiles(TestFakeProduct.class);
    assertThat(classes).hasSize(1);

    classes = findTestsForChangedFiles(ANewClass.class);
    assertThat(classes).isEmpty();
  }

  @Test
  void testSwingSubclass() {
    Set<File> fileSet = setify(getFileForClass(TestFakeProduct.class));
    fileSet.add(InfinitestTestUtils.getFileForClass(TestFakeTree.class));
    assertThat(getGraph().findTestsToRun(fileSet)).as("Tests were not added to the dependency graph").hasSize(2);
    
    fileSet = setify(InfinitestTestUtils.getFileForClass(FakeProduct.class));
    Set<JavaClass> tests = getGraph().findTestsToRun(fileSet);
    assertThat(tests)
    .as("Parents not found?").hasSize(2)
    .as("Required test not found").contains(new FakeJavaClass(TestFakeProduct.class.getName()))
    .as("Required test not found").contains(new FakeJavaClass(TestFakeTree.class.getName()));
  }
}
