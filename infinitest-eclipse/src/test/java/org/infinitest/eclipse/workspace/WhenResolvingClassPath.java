/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2012
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
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
