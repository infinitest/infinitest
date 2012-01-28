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

import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javassist.*;
import junit.framework.*;

import org.infinitest.util.*;
import org.junit.Test;

import com.fakeco.fakeproduct.*;
import com.fakeco.fakeproduct.id.*;

public class WhenLookingForDependenciesBetweenClasses extends DependencyGraphTestBase {
	@Test
	public void shouldFindDependenciesCreatedByAnnotations() {
		findTestsForChangedFiles(FakeProduct.class, TestFakeProduct.class);
		verifyDependency(MethodAnnotation.class, TestFakeProduct.class);
		verifyDependency(ClassAnnotation.class, TestFakeProduct.class);
		verifyDependency(FieldAnnotation.class, TestFakeProduct.class);
	}

	@Test
	public void shouldFindDependenciesCreatedByEnums() {
		addToDependencyGraph(FakeProduct.class, TestFakeProduct.class);
		verifyDependency(FakeEnum.class, TestFakeProduct.class);
	}

	@Test
	public void shouldFindDependenciesThatRouteThroughLibraries() {
		addToDependencyGraph(FakeId.class, FakeProduct.class, TestFakeProduct.class);
		verifyDependency(FakeId.class, TestFakeProduct.class);
	}

	@Test
	public void canClearIndex() {
		File testFakeProductFile = InfinitestTestUtils.getFileForClass(TestFakeProduct.class);
		Set<File> fileSet = setify(testFakeProductFile);
		fileSet.add(InfinitestTestUtils.getFileForClass(FakeProduct.class));
		getGraph().findTestsToRun(fileSet);
		getGraph().clear();
		fileSet.remove(testFakeProductFile);
		assertEquals(1, fileSet.size());

		Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
		assertFalse("We cleared TestFakeProduct from the graph", testsToRun.contains(testFakeProductFile));
		assertEquals("Because FakeProduct is the only class in the graph, no tests should be returned", 0, testsToRun.size());
	}

	@Test
	public void shouldRunTestsForTransitiveDependencies() {
		addToDependencyGraph(FakeTree.class, FakeDependency.class, TestFakeTree.class);
		verifyDependency(FakeTree.class, TestFakeTree.class);
		verifyDependency(FakeDependency.class, TestFakeTree.class);
	}

	@Test
	public void canFilterTests() {
		Set<File> fileSet = setify(getFileForClass(TestFakeProduct.class));
		Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
		assertEquals("TestFakeProduct should have been found", 1, testsToRun.size());

		addFilter(TestFakeProduct.class.getName());
		testsToRun = getGraph().findTestsToRun(fileSet);
		assertEquals("TestFakeProduct should have been filtered", 0, testsToRun.size());
	}

	@Test
	public void shouldFindJUnit4Tests() {
		assertClassRecognizedAsTest(TestJUnit4TestCase.class);
	}

	@Test
	public void shouldFindJUnit4TestsThatInheritAllTheirTestMethods() {
		assertClassRecognizedAsTest(JUnit4TestThatInherits.class);
	}

	@Test
	public void shouldFindNewlyCreatedClasses() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		pool.appendPathList(systemClasspath());
		CtClass cc = pool.makeClass("ANewTest");
		cc.setSuperclass(pool.get(TestCase.class.getName()));
		cc.addMethod(CtMethod.make("public void testSomething(){}", cc));
		cc.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
		Class<?> testClass = cc.toClass();
		InfinitestTestUtils.getFileForClass(testClass).deleteOnExit();
		assertClassRecognizedAsTest(testClass);
	}

	@Test
	public void testJUnit3Tests() {
		assertClassRecognizedAsTest(TestJunit3TestCase.class);
	}

	@Test
	public void testClassesOutsideCollection() {
		assertFalse(getGraph().findJavaClass(Object.class.getName()).locatedInClassFile());
		assertTrue(getGraph().findJavaClass(FakeTree.class.getName()).locatedInClassFile());
		assertFalse("The dependency graph should not index class files outside the target directory", getGraph().isIndexed(Object.class));
	}

	@Test
	public void testEquality() {
		JavaClass class1 = new FakeJavaClass("com.fakeco.fakeproduct.FakeProduct$StaticInnerClass");
		JavaClass class2 = new FakeJavaClass("com.fakeco.fakeproduct.FakeProduct");
		JavaClass class3 = new FakeJavaClass("com.fakeco.fakeproduct.FakeProduct$StaticInnerClass");
		assertFalse(class1.equals(class2));
		assertEquals(class1, class3);
	}

	@Test
	public void shouldDetectDirectDependencies() {
		addToDependencyGraph(FakeProduct.class, TestFakeProduct.class);
		verifyDependency(FakeProduct.class, TestFakeProduct.class);
	}

	@Test
	public void testInheritFromFilteredTest() {
		addFilter(TestFakeProduct.class.getName());
		Set<JavaClass> tests = getGraph().findTestsToRun(setify(InfinitestTestUtils.getFileForClass(TestThatInherits.class)));
		assertFalse("Filtered test found", tests.contains(new FakeJavaClass(TestFakeProduct.class.getName())));
		assertTrue("Required test not found", tests.contains(new FakeJavaClass(TestThatInherits.class.getName())));
	}

	@Test
	public void testThreeWayIntraPackageDependency() {
		Class<FakeProduct> changedFile = FakeProduct.class;
		Class<TestFakeTree> expectedTest = TestFakeTree.class;
		addToDependencyGraph(changedFile, FakeTree.class, expectedTest);
		verifyDependency(changedFile, expectedTest);
	}

	@Test
	public void shouldDetectNewClass() {
		Set<JavaClass> classes = findTestsForChangedFiles(FakeProduct.class);
		assertTrue(classes.isEmpty());

		classes = findTestsForChangedFiles(TestFakeProduct.class);
		assertEquals(1, classes.size());

		classes = findTestsForChangedFiles(ANewClass.class);
		assertThat(classes, equalTo(Collections.<JavaClass> emptySet()));
	}

	@Test
	public void testSwingSubclass() {
		Set<File> fileSet = setify(getFileForClass(TestFakeProduct.class));
		fileSet.add(InfinitestTestUtils.getFileForClass(TestFakeTree.class));
		assertEquals("Tests were not added to the dependency graph", 2, getGraph().findTestsToRun(fileSet).size());

		fileSet = setify(InfinitestTestUtils.getFileForClass(FakeProduct.class));
		Set<JavaClass> tests = getGraph().findTestsToRun(fileSet);
		assertEquals("Parents not found?", 2, tests.size());
		assertTrue("Required test not found", tests.contains(new FakeJavaClass(TestFakeProduct.class.getName())));
		assertTrue("Required test not found", tests.contains(new FakeJavaClass(TestFakeTree.class.getName())));
	}
}
