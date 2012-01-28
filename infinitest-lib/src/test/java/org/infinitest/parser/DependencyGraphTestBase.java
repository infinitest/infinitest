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
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javassist.*;

import org.infinitest.filter.*;
import org.infinitest.util.*;
import org.junit.*;

public abstract class DependencyGraphTestBase {
	private ClassFileTestDetector testDetector = null;
	private FilterStub filter;

	@Before
	public final void setUp() {
		filter = new FilterStub();
		testDetector = new ClassFileTestDetector(filter);
		testDetector.setClasspathProvider(fakeClasspath());
		testDetector.findTestsToRun(Collections.<File> emptySet());
	}

	@After
	public final void tearDown() {
		testDetector = null;
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

	protected void addDependency(String parent, String child) throws NotFoundException, CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass parentClass = pool.get(parent);
		CtClass childClass = pool.get(child);
		parentClass.addField(new CtField(childClass, "dep", parentClass));
		parentClass.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
	}

	protected void assertClassRecognizedAsTest(Class<?> testClass) {
		Set<File> fileSet = setify(InfinitestTestUtils.getFileForClass(testClass));
		Set<JavaClass> testsToRun = getGraph().findTestsToRun(fileSet);
		assertEquals(testClass.getSimpleName() + " should have been recognized as a test", 1, testsToRun.size());
		JavaClass testToRun = testsToRun.iterator().next();
		assertEquals(testClass.getName(), testToRun.getName());
		assertTrue(testToRun.isATest());
	}

	protected void verifyDependency(Class<?> changedFile, Class<?> expectedTest) {
		Set<JavaClass> testsToRun = findTestsForChangedFiles(changedFile);
		assertTrue("Changing " + changedFile + " did not cause " + expectedTest + " to be run", testsToRun.contains(getGraph().findJavaClass(expectedTest.getName())));
	}

	protected ClassFileTestDetector getGraph() {
		return testDetector;
	}

	protected void addFilter(String className) {
		filter.addClass(className);
	}

}
