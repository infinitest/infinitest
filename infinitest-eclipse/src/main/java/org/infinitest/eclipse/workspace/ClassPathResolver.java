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

import static com.google.common.base.Joiner.*;
import static java.io.File.*;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

import com.google.common.collect.ImmutableList;
import com.google.inject.internal.Lists;

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
