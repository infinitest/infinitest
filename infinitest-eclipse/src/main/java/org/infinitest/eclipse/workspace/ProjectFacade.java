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

import static java.util.logging.Level.WARNING;
import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.jdt.core.IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;
import static org.eclipse.jdt.launching.JavaRuntime.getVMInstall;
import static org.infinitest.util.InfinitestUtils.log;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;

class ProjectFacade implements EclipseProject {
	private final IJavaProject project;

	public ProjectFacade(IJavaProject project) {
		this.project = project;
	}

	/**
	 * Returns a File that corresponds to the absolute location of the project's
	 * root directory.
	 */
	public File workingDirectory() {
		return project.getProject().getLocation().toFile();
	}

	// RISK Untested
	public File getJvmHome() throws CoreException {
		IVMInstall jvmInstall = getVMInstall(project);

		try {
			int majorVersion = parseMajorVersion(jvmInstall);
			if (majorVersion < 5) {
				jvmInstall = findFirstJava5OrGreaterJvm(jvmInstall);
			}
		} catch (UnknownJvmVersionException e) {
			log(WARNING, "Error determining JVM version (" + e.getVersion() + "). Using default version.");
		}

		if ((jvmInstall != null) && jvmInstall.getInstallLocation().exists()) {
			return jvmInstall.getInstallLocation();
		}

		log(WARNING, "Cannot find any JVM. Using the one from JAVA_HOME.");
		return new File(System.getProperty("java.home"));
	}

	private IVMInstall findFirstJava5OrGreaterJvm(IVMInstall jvmInstall) throws UnknownJvmVersionException {
		IVMInstall[] installs = jvmInstall.getVMInstallType().getVMInstalls();
		for (IVMInstall candidate : installs) {
			if (parseMajorVersion(candidate) >= 5) {
				return candidate;
			}
		}
		return null;
	}

	protected int parseMajorVersion(IVMInstall jvmInstall) throws UnknownJvmVersionException {
		if (!(jvmInstall instanceof IVMInstall2)) {
			throw new UnknownJvmVersionException();
		}

		String version = ((IVMInstall2) jvmInstall).getJavaVersion();

		if ((version == null) || (version.length() < 3)) {
			throw new UnknownJvmVersionException(version);
		}

		try {
			if (version.startsWith("1.")) {
				return Integer.parseInt(version.substring(2, 3));
			}
			return Integer.parseInt(version.substring(0, version.indexOf('.')));
		} catch (NumberFormatException e) {
			throw new UnknownJvmVersionException(version);
		}
	}

	private static class UnknownJvmVersionException extends Exception {
		private static final long serialVersionUID = 9053017151840025522L;
		
		private String version;

		public UnknownJvmVersionException() {
		}
		
		public UnknownJvmVersionException(String version) {
			this.version = version;
		}
		
		public String getVersion() {
			return version;
		}
	}

	public boolean isOpen() {
		return project.getProject().isOpen();
	}

	/**
	 * Returns a URI that can be used to uniquely identify a particular project.
	 */
	@Override
	public URI getLocationURI() {
		return project.getProject().getLocationURI();
	}

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		try {
			return project.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		return project.getElementName();
	}

	public boolean hasErrors() throws CoreException {
		int severity = project.getProject().findMaxProblemSeverity(JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_INFINITE);
		return severity == SEVERITY_ERROR;
	}

	public String rawClasspath() throws CoreException {
		return new ClassPathResolver(new EclipseFacade()).rawClasspath(project);
	}

	@Override
	public IPath getDefaultOutputLocation() {
		try {
			return project.getOutputLocation();
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return <code>true</code> if this class is on the project's classpath or part of the project
	 */
	public boolean isOnClasspath(IResource resource) {
		// isOnClasspath() does not seem to return true for a class in its own project
		return project.isOnClasspath(resource) || resource.getProject().equals(project.getProject());
	}
	
	public boolean contains(IResource resource) {
		return resource.getProject().equals(project.getProject());
	}
}
