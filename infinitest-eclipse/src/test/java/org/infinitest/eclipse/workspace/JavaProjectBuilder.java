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

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_LIBRARY;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_PROJECT;
import static org.eclipse.jdt.core.IClasspathEntry.CPE_SOURCE;
import static org.eclipse.jdt.core.IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;
import static org.eclipse.jdt.core.IPackageFragmentRoot.K_BINARY;
import static org.eclipse.jdt.core.IPackageFragmentRoot.K_SOURCE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.eval.IEvaluationContext;

public class JavaProjectBuilder implements IJavaProject {
	public static final String PATH_TO_WORKSPACE = "/path/to/workspace/";

	private final String projectName;
	private final List<IClasspathEntry> entries;

	private final String defaultOutputLocation = "/target/classes/";

	private IMarker marker;

	public static JavaProjectBuilder project() {
		return project("project");
	}

	public static JavaProjectBuilder project(String projectName) {
		return new JavaProjectBuilder(projectName);
	}

	private JavaProjectBuilder(String projectName) {
		this.projectName = projectName;
		entries = new ArrayList<>();
	}

	public JavaProjectBuilder withJar(String jarName) {
		return addInternalJar(jarName, false);
	}

	public JavaProjectBuilder andProjectJar(String projectName, String jarName) {
		return addInternalJar(projectName, jarName, false);
	}

	public JavaProjectBuilder andExportedJar(String jarName) {
		return addInternalJar(jarName, true);
	}

	private JavaProjectBuilder addInternalJar(String jarProjectName, String jarName, boolean exported) {
		String path = "/" + jarProjectName + "/" + jarName + ".jar";
		return addJar(path, exported);
	}

	private JavaProjectBuilder addInternalJar(String jarName, boolean exported) {
		return addInternalJar(projectName, jarName, exported);
	}

	public JavaProjectBuilder withMarker(IMarker aMarker) {
		marker = aMarker;
		return this;
	}

	public JavaProjectBuilder andExtenralJar(String externalJarPath) {
		return addJar(externalJarPath, false);
	}

	private JavaProjectBuilder addJar(String path, boolean exported) {
		entries.add(new EntryBuilder().contentKind(K_BINARY).entryKind(CPE_LIBRARY).path(new Path(path)).exported(exported).build());

		return this;
	}

	public JavaProjectBuilder andSourceDirectory(String sourceDirectory, String outputDirectory) {
		return andSourceDirectory(sourceDirectory, outputDirectory, false);
	}

	public IJavaProject andExportedSourceDirectory(String sourceDirectory, String outputDirectory) {
		return andSourceDirectory(sourceDirectory, outputDirectory, true);
	}

	public JavaProjectBuilder andSourceDirectory(String sourceDirectory, String outputDirectory, boolean exported) {
		Path sourcePath = projectPath(sourceDirectory);
		Path outputPath = projectPath(outputDirectory);

		entries.add(new EntryBuilder().contentKind(K_SOURCE).entryKind(CPE_SOURCE).path(sourcePath).outputPath(outputPath).exported(exported).build());

		return this;
	}

	public JavaProjectBuilder andSourceDirectory(String sourceDirectory) {
		Path sourcePath = projectPath(sourceDirectory);

		entries.add(new EntryBuilder().contentKind(K_SOURCE).entryKind(CPE_SOURCE).path(sourcePath).build());

		return this;
	}

	private Path projectPath(String path) {
		return new Path("/" + projectName + "/" + path);
	}

	public JavaProjectBuilder andDependsOn(String dependencyProjectName) {
		entries.add(new EntryBuilder().contentKind(K_SOURCE).entryKind(CPE_PROJECT).path(new Path("/" + dependencyProjectName)).build());

		return this;
	}

	// Implemented IJavaProject methods

	@Override
	public IClasspathEntry[] getResolvedClasspath(boolean b) {
		return entries.toArray(new IClasspathEntry[entries.size()]);
	}

	@Override
	public IResource getCorrespondingResource() {
		IResource resource = mock(IResource.class);
		when(resource.getLocation()).thenReturn(new Path(projectName));
		when(resource.getFullPath()).thenReturn(getPath());
		return resource;
	}

	private void addMarker(IResource resource) {
		if (marker != null) {
			try {
				when(resource.createMarker(anyString())).thenReturn(marker);
				resource.deleteMarkers(anyString(), eq(true), eq(DEPTH_INFINITE));
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public IPath getOutputLocation() {
		return new Path("/" + projectName + defaultOutputLocation);
	}

	@Override
	public IPath getPath() {
		return new Path("/" + projectName);
	}

	@Override
	public IResource getResource() {
		return getCorrespondingResource();
	}

	@Override
	public IProject getProject() {
		try {
			IProject project = mock(IProject.class);
			when(project.getLocationURI()).thenReturn(new URI("/root/" + projectName));
			when(project.getLocation()).thenReturn(new Path("/root/" + projectName));
			when(project.getName()).thenReturn(projectName);
			when(project.findMaxProblemSeverity(JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_INFINITE)).thenReturn(-1);
			addMarker(project);
			return project;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getElementName() {
		// No idea if this is right
		return projectName;
	}

	@Override
	public IClasspathEntry[] getRawClasspath() {
		return getResolvedClasspath(false);
	}

	// Below are the non-supported IProject interface classes. Nothing to see
	// here.

	@Override
	public IClasspathEntry decodeClasspathEntry(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeClasspathEntry(IClasspathEntry iClasspathEntry) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement findElement(IPath iPath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement findElement(IPath iPath, WorkingCopyOwner workingCopyOwner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement findElement(String s, WorkingCopyOwner workingCopyOwner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragment findPackageFragment(IPath iPath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragmentRoot findPackageFragmentRoot(IPath iPath) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry iClasspathEntry) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, WorkingCopyOwner workingCopyOwner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, WorkingCopyOwner workingCopyOwner, IProgressMonitor iProgressMonitor)

	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, String s1, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, String s1, WorkingCopyOwner workingCopyOwner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IType findType(String s, String s1, WorkingCopyOwner workingCopyOwner, IProgressMonitor iProgressMonitor)

	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragmentRoot[] getAllPackageFragmentRoots() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] getNonJavaResources() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getOption(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<?, ?> getOptions(boolean b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragmentRoot getPackageFragmentRoot(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragmentRoot getPackageFragmentRoot(IResource iResource) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragmentRoot[] getPackageFragmentRoots() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public IPackageFragmentRoot[] getPackageFragmentRoots(IClasspathEntry iClasspathEntry) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPackageFragment[] getPackageFragments() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getRequiredProjectNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasBuildState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasClasspathCycle(IClasspathEntry[] iClasspathEntries) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOnClasspath(IJavaElement iJavaElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOnClasspath(IResource iResource) {
		return true;
	}

	@Override
	public IEvaluationContext newEvaluationContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IRegion iRegion, IProgressMonitor iProgressMonitor)

	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IRegion iRegion, WorkingCopyOwner workingCopyOwner, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IType iType, IRegion iRegion, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(IType iType, IRegion iRegion, WorkingCopyOwner workingCopyOwner, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPath readOutputLocation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClasspathEntry[] readRawClasspath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOption(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOptions(Map map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOutputLocation(IPath iPath, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawClasspath(IClasspathEntry[] iClasspathEntries, IPath iPath, boolean b, IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawClasspath(IClasspathEntry[] iClasspathEntries, boolean b, IProgressMonitor iProgressMonitor)

	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawClasspath(IClasspathEntry[] iClasspathEntries, IProgressMonitor iProgressMonitor)

	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRawClasspath(IClasspathEntry[] iClasspathEntries, IPath iPath, IProgressMonitor iProgressMonitor)

	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement[] getChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement getAncestor(int i) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAttachedJavadoc(IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getElementType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHandleIdentifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaModel getJavaModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaProject getJavaProject() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IOpenable getOpenable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement getParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IJavaElement getPrimaryElement() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IResource getUnderlyingResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isStructureKnown() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAdapter(Class aClass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String findRecommendedLineSeparator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBuffer getBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasUnsavedChanges() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConsistent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void makeConsistent(IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void open(IProgressMonitor iProgressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void save(IProgressMonitor iProgressMonitor, boolean b) {
		throw new UnsupportedOperationException();
	}
}
