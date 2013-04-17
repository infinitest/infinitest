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
package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.jdt.core.*;
import org.junit.*;

public class JavaProjectSetTest {
	private IJavaProject project;
	private JavaProjectSet projectSet;

	@Before
	public void inContext() {
		URL resource = getClass().getResource("/emptyJar.jar");
		File externalJar = new File(resource.getPath());
		String externalJarPath = externalJar.getAbsolutePath();

		project = project("projectA").withJar("lib/someJarFile").andExportedJar("lib/someExportedJarFile").andExtenralJar(externalJarPath).andSourceDirectory("src", "srcbin").andDependsOn("projectB");
		ResourceFinder finder = mock(ResourceFinder.class);
		List<IJavaProject> projects = newArrayList(project);
		when(finder.getJavaProjects()).thenReturn(projects);
		projectSet = new JavaProjectSet(finder);
	}

	@Test
	public void shouldProvideOutputDirectoriesForProjectAsAbsolutePaths() throws JavaModelException {
		ArrayList<File> expectedDirectories = newArrayList(new File("/root/projectA/srcbin"), new File("/root/projectA/target/classes"));

		assertEquals(expectedDirectories, projectSet.outputDirectories(new ProjectFacade(project)));
	}
}
