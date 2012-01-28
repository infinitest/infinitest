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
import static org.infinitest.util.InfinitestTestUtils.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import javassist.*;

import org.infinitest.util.*;
import org.junit.*;

import com.fakeco.fakeproduct.*;

public class WhenClassesChange extends DependencyGraphTestBase {
	private File backup;

	@Before
	public void inContext() throws Exception {
		backup = createBackup(TestAlmostNotATest.class.getName());
	}

	@After
	public void cleanupContext() {
		restoreFromBackup(backup);
	}

	@Test
	public void shouldRecognizeWhenTestsAreChangedToRegularClasses() throws Exception {
		Class<?> testClass = TestAlmostNotATest.class;
		JavaClass javaClass = runTest(testClass);
		assertTrue("Inital state is incorrect", javaClass.isATest());

		untestify();
		updateGraphWithChangedClass(testClass);
		assertThat(getGraph().getCurrentTests(), equalTo(Collections.<String> emptySet()));

		javaClass = getGraph().findJavaClass(testClass.getName());
		assertFalse("Class was not reloaded", javaClass.isATest());
	}

	private JavaClass runTest(Class<?> testClass) {
		Set<JavaClass> classes = updateGraphWithChangedClass(testClass);
		assertEquals(1, classes.size());
		return classes.iterator().next();
	}

	private Set<JavaClass> updateGraphWithChangedClass(Class<?> testClass) {
		File classFile = getFileForClass(testClass);
		return getGraph().findTestsToRun(setify(classFile));
	}

	private void untestify() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass(TestAlmostNotATest.class.getName());
		cc.setSuperclass(pool.get(Object.class.getName()));
		cc.writeFile(FakeEnvironments.fakeClassDirectory().getAbsolutePath());
	}
}
