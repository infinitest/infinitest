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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WhenResolvingClassPath {
	@InjectMocks
	private ClassPathResolver classPathResolver;

	@Mock
	private EclipseFacade eclipseFacade;
	@Mock
	private IJavaProject project;
	@Mock
	private IRuntimeClasspathEntry runtimeClasspathEntry;
	@Mock
	private IPath path;
	@Mock
	private IPath jarPath;
	@Mock
	private IClasspathContainer classpathContainer;
	@Mock
	private IClasspathEntry classpathEntry;

	@Test
	void shouldFindDefaultRuntimeClassPathWithOneJar() throws CoreException {
		when(eclipseFacade.computeDefaultRuntimeClassPath(project)).thenReturn(new String[]{"1.jar"});
		when(eclipseFacade.computeUnresolvedRuntimeClasspath(project)).thenReturn(new IRuntimeClasspathEntry[0]);

		String classpath = classPathResolver.rawClasspath(project);

		assertThat(classpath).isEqualTo("1.jar");
	}

	@Test
	void shouldFindDefaultRuntimeClassPathWithTwoJars() throws CoreException {
		when(eclipseFacade.computeDefaultRuntimeClassPath(project)).thenReturn(new String[]{"1.jar", "2.jar"});
		when(eclipseFacade.computeUnresolvedRuntimeClasspath(project)).thenReturn(new IRuntimeClasspathEntry[0]);

		String classpath = classPathResolver.rawClasspath(project);

		assertThat(classpath).isEqualTo("1.jar" + File.pathSeparator + "2.jar");
	}

	@Test
	void shouldAddUnresolvedRuntimeJars() throws CoreException {
		when(eclipseFacade.computeDefaultRuntimeClassPath(project)).thenReturn(new String[]{"1.jar", "2.jar"});
		when(eclipseFacade.computeUnresolvedRuntimeClasspath(project)).thenReturn(new IRuntimeClasspathEntry[]{runtimeClasspathEntry});
		when(runtimeClasspathEntry.getVariableName()).thenReturn("SCALA_CONTAINER");
		when(runtimeClasspathEntry.getPath()).thenReturn(path);
		when(eclipseFacade.getClasspathContainer(path, project)).thenReturn(classpathContainer);
		when(classpathContainer.getClasspathEntries()).thenReturn(new IClasspathEntry[]{classpathEntry});
		when(classpathEntry.getPath()).thenReturn(jarPath);
		when(jarPath.toString()).thenReturn("3.jar");

		String classpath = classPathResolver.rawClasspath(project);

		assertThat(classpath).isEqualTo("1.jar" + File.pathSeparator + "2.jar" + File.pathSeparator + "3.jar");
	}

	@Test
	void shouldIgnoreJreContainer() throws CoreException {
		when(eclipseFacade.computeDefaultRuntimeClassPath(project)).thenReturn(new String[0]);
		when(eclipseFacade.computeUnresolvedRuntimeClasspath(project)).thenReturn(new IRuntimeClasspathEntry[]{runtimeClasspathEntry});
		when(runtimeClasspathEntry.getVariableName()).thenReturn("org.eclipse.jdt.launching.JRE_CONTAINER");

		String classpath = classPathResolver.rawClasspath(project);

		assertThat(classpath).isEmpty();
	}

	@Test
	void shouldIgnoreNonJar() throws CoreException {
		when(eclipseFacade.computeDefaultRuntimeClassPath(project)).thenReturn(new String[0]);
		when(eclipseFacade.computeUnresolvedRuntimeClasspath(project)).thenReturn(new IRuntimeClasspathEntry[]{runtimeClasspathEntry});
		when(runtimeClasspathEntry.getVariableName()).thenReturn("SCALA_CONTAINER");

		String classpath = classPathResolver.rawClasspath(project);

		assertThat(classpath).isEmpty();
	}

	@Test
	void shouldIgnoreCase() throws CoreException {
		when(eclipseFacade.computeDefaultRuntimeClassPath(project)).thenReturn(new String[0]);
		when(eclipseFacade.computeUnresolvedRuntimeClasspath(project)).thenReturn(new IRuntimeClasspathEntry[]{runtimeClasspathEntry});
		when(runtimeClasspathEntry.getVariableName()).thenReturn("SCALA_CONTAINER");
		when(runtimeClasspathEntry.getPath()).thenReturn(path);
		when(eclipseFacade.getClasspathContainer(path, project)).thenReturn(classpathContainer);
		when(classpathContainer.getClasspathEntries()).thenReturn(new IClasspathEntry[]{classpathEntry});
		when(classpathEntry.getPath()).thenReturn(jarPath);
		when(jarPath.toString()).thenReturn("3.JAR");

		String classpath = classPathResolver.rawClasspath(project);

		assertThat(classpath).isEqualTo("3.JAR");
	}
}
