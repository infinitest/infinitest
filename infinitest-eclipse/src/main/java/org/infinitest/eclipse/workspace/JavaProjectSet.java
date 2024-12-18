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

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
class JavaProjectSet implements ProjectSet {
	private final ResourceFinder finder;

	@Autowired
	public JavaProjectSet(ResourceFinder finder) {
		this.finder = finder;
	}

	@Override
	public ProjectFacade findProject(IPath path) {
		return openProjects()
				.filter(project -> project.getPath().equals(path))
				.map(ProjectFacade::new)
				.findFirst()
				.orElse(null);
	}

	protected ProjectFacade createProjectFacade(IJavaProject project) {
		return new ProjectFacade(project);
	}

	File workingDirectory(IJavaProject project) throws JavaModelException {
		return project.getCorrespondingResource().getLocation().toFile();
	}

	@Override
	public List<ProjectFacade> projects() {
		return openProjects()
				.map(ProjectFacade::new)
				.toList();
	}

	@Override
	public boolean hasErrors() throws CoreException {
		for (ProjectFacade project : projects()) {
			if (project.hasErrors()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the {@link Stream} of the open projects
	 */
	private Stream<IJavaProject> openProjects() {
		return finder.getJavaProjects().stream().filter(IJavaProject::isOpen);
	}

	/**
	 * Return a list of Files that represent of all of the project's output
	 * directories. This includes the project's default output directory, as
	 * well as any custom output directories. All Files are absolute.
	 * 
	 * @throws JavaModelException
	 */
	@Override
	public List<File> outputDirectories(EclipseProject project) throws JavaModelException {
		// I suspect there's something, somewhere in the Eclipse SDK that will
		// find this for us.
		List<File> outputDirectories = customOutputDirectoriesFor(project);

		outputDirectories.add(absoluteFile(project.getDefaultOutputLocation()));

		return outputDirectories;
	}

	private List<File> customOutputDirectoriesFor(EclipseProject project) {
		List<File> outputDirectories = new ArrayList<>();
		for (IClasspathEntry each : project.getClasspathEntries()) {
			IPath outputLocation = each.getOutputLocation();
			if (outputLocation != null) {
				File file = absoluteFile(outputLocation);
				outputDirectories.add(file);
			}
		}
		return outputDirectories;
	}

	private File absoluteFile(IPath path) {
		File file = new File(path.toPortableString());
		if (file.exists()) {
			return file;
		}

		file = finder.findFileFor(path);
		if (file != null) {
			return file;
		}

		String jarPath = relativePath(path);

		Path projectPath = new Path("/" + path.segment(0));
		ProjectFacade project = findProject(projectPath);
		return new File(project.workingDirectory(), jarPath);
	}

	private String relativePath(IPath path) {
		return path.removeFirstSegments(1).toPortableString();
	}
}
