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

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;
import org.infinitest.util.*;
import org.junit.*;
import org.junit.runner.*;

@RunWith(AutoMockRunner.class)
public class WhenResolvingClassPath {
	private EclipseFacade mockEclipseFacade;
	private ClassPathResolver classPathResolver;
	private IJavaProject mockProject;
	private IRuntimeClasspathEntry mockRuntimeClasspathEntry;
	private IPath mockPath;
	private IPath mockJarPath;
	private IClasspathContainer mockIClasspathContainer;
	private IClasspathEntry mockIClasspathEntry;

	@Before
	public void inContext() {
		classPathResolver = new ClassPathResolver(mockEclipseFacade);
	}

	@Test
	public void shouldFindDefaultRuntimeClassPathWithOneJar() throws CoreException {
		when(mockEclipseFacade.computeDefaultRuntimeClassPath(mockProject)).thenReturn(new String[] { "1.jar" });
		when(mockEclipseFacade.computeUnresolvedRuntimeClasspath(mockProject)).thenReturn(new IRuntimeClasspathEntry[0]);

		String classpath = classPathResolver.rawClasspath(mockProject);

		assertThat(classpath).isEqualTo("1.jar");
	}

	@Test
	public void shouldFindDefaultRuntimeClassPathWithTwoJars() throws CoreException {
		when(mockEclipseFacade.computeDefaultRuntimeClassPath(mockProject)).thenReturn(new String[] { "1.jar", "2.jar" });
		when(mockEclipseFacade.computeUnresolvedRuntimeClasspath(mockProject)).thenReturn(new IRuntimeClasspathEntry[0]);

		String classpath = classPathResolver.rawClasspath(mockProject);

		assertThat(classpath).isEqualTo("1.jar:2.jar");
	}

	@Test
	public void shouldAddUnresolvedRuntimeJars() throws CoreException {
		when(mockEclipseFacade.computeDefaultRuntimeClassPath(mockProject)).thenReturn(new String[] { "1.jar", "2.jar" });
		when(mockEclipseFacade.computeUnresolvedRuntimeClasspath(mockProject)).thenReturn(new IRuntimeClasspathEntry[] { mockRuntimeClasspathEntry });
		when(mockRuntimeClasspathEntry.getVariableName()).thenReturn("SCALA_CONTAINER");
		when(mockRuntimeClasspathEntry.getPath()).thenReturn(mockPath);
		when(mockEclipseFacade.getClasspathContainer(mockPath, mockProject)).thenReturn(mockIClasspathContainer);
		when(mockIClasspathContainer.getClasspathEntries()).thenReturn(new IClasspathEntry[] { mockIClasspathEntry });
		when(mockIClasspathEntry.getPath()).thenReturn(mockJarPath);
		when(mockJarPath.toString()).thenReturn("3.jar");

		String classpath = classPathResolver.rawClasspath(mockProject);

		assertThat(classpath).isEqualTo("1.jar:2.jar:3.jar");
	}

	@Test
	public void shouldIgnoreJreContainer() throws CoreException {
		when(mockEclipseFacade.computeDefaultRuntimeClassPath(mockProject)).thenReturn(new String[0]);
		when(mockEclipseFacade.computeUnresolvedRuntimeClasspath(mockProject)).thenReturn(new IRuntimeClasspathEntry[] { mockRuntimeClasspathEntry });
		when(mockRuntimeClasspathEntry.getVariableName()).thenReturn("org.eclipse.jdt.launching.JRE_CONTAINER");
		when(mockRuntimeClasspathEntry.getPath()).thenReturn(mockPath);
		when(mockEclipseFacade.getClasspathContainer(mockPath, mockProject)).thenReturn(mockIClasspathContainer);
		when(mockIClasspathContainer.getClasspathEntries()).thenReturn(new IClasspathEntry[] { mockIClasspathEntry });
		when(mockIClasspathEntry.getPath()).thenReturn(mockJarPath);
		when(mockJarPath.toString()).thenReturn("3.jar");

		String classpath = classPathResolver.rawClasspath(mockProject);

		assertThat(classpath).isEmpty();
	}

	@Test
	public void shouldIgnoreNonJar() throws CoreException {
		when(mockEclipseFacade.computeDefaultRuntimeClassPath(mockProject)).thenReturn(new String[0]);
		when(mockEclipseFacade.computeUnresolvedRuntimeClasspath(mockProject)).thenReturn(new IRuntimeClasspathEntry[] { mockRuntimeClasspathEntry });
		when(mockRuntimeClasspathEntry.getVariableName()).thenReturn("SCALA_CONTAINER");
		when(mockRuntimeClasspathEntry.getPath()).thenReturn(mockPath);
		when(mockEclipseFacade.getClasspathContainer(mockPath, mockProject)).thenReturn(mockIClasspathContainer);
		when(mockIClasspathContainer.getClasspathEntries()).thenReturn(new IClasspathEntry[] { mockIClasspathEntry });
		when(mockIClasspathEntry.getPath()).thenReturn(mockJarPath);
		when(mockJarPath.toString()).thenReturn("3.zip");

		String classpath = classPathResolver.rawClasspath(mockProject);

		assertThat(classpath).isEmpty();
	}

	@Test
	public void shouldIgnoreCase() throws CoreException {
		when(mockEclipseFacade.computeDefaultRuntimeClassPath(mockProject)).thenReturn(new String[0]);
		when(mockEclipseFacade.computeUnresolvedRuntimeClasspath(mockProject)).thenReturn(new IRuntimeClasspathEntry[] { mockRuntimeClasspathEntry });
		when(mockRuntimeClasspathEntry.getVariableName()).thenReturn("SCALA_CONTAINER");
		when(mockRuntimeClasspathEntry.getPath()).thenReturn(mockPath);
		when(mockEclipseFacade.getClasspathContainer(mockPath, mockProject)).thenReturn(mockIClasspathContainer);
		when(mockIClasspathContainer.getClasspathEntries()).thenReturn(new IClasspathEntry[] { mockIClasspathEntry });
		when(mockIClasspathEntry.getPath()).thenReturn(mockJarPath);
		when(mockJarPath.toString()).thenReturn("3.JAR");

		String classpath = classPathResolver.rawClasspath(mockProject);

		assertThat(classpath).isEqualTo("3.JAR");
	}
}
