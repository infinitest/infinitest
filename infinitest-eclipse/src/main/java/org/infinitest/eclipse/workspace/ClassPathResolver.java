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

import static com.google.common.base.Joiner.*;
import static java.io.File.*;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

import com.google.common.collect.*;

public class ClassPathResolver {
	private static final String JRE_CONTAINER = "org.eclipse.jdt.launching.JRE_CONTAINER";

	private final EclipseFacade eclipseFacade;

	public ClassPathResolver(EclipseFacade eclipseFacade) {
		this.eclipseFacade = eclipseFacade;
	}

	public String rawClasspath(IJavaProject project) throws CoreException {
		List<String> classpath = ImmutableList.<String> builder() //
				.add(runtimeClassPath(project)) //
				.addAll(unresolvedRuntimeClasspath(project)).build();

		return on(pathSeparatorChar).join(classpath);
	}

	String[] runtimeClassPath(IJavaProject project) throws CoreException {
		return eclipseFacade.computeDefaultRuntimeClassPath(project);
	}

	Iterable<String> unresolvedRuntimeClasspath(IJavaProject project) throws CoreException {
		List<String> jars = Lists.newArrayList();

		for (IRuntimeClasspathEntry runtimeEntry : eclipseFacade.computeUnresolvedRuntimeClasspath(project)) {
			String variable = runtimeEntry.getVariableName();
			if ((null != variable) && !variable.contains(JRE_CONTAINER)) {
				IClasspathContainer container = eclipseFacade.getClasspathContainer(runtimeEntry.getPath(), project);
				if (null != container) {
					for (IClasspathEntry entry : container.getClasspathEntries()) {
						String path = entry.getPath().toString();
						if (path.matches(".*\\.[Jj][Aa][Rr]\\z")) {
							jars.add(path);
						}
					}
				}
			}
		}

		return jars;
	}
}
