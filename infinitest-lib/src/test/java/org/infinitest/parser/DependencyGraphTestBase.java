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
