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
package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IResource.*;
import static org.eclipse.jdt.core.IClasspathEntry.*;
import static org.eclipse.jdt.core.IJavaModelMarker.*;
import static org.eclipse.jdt.core.IPackageFragmentRoot.*;

import java.net.URI;
import java.net.URISyntaxException;
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

public class JavaProjectBuilder implements IJavaProject
{
    public static final String PATH_TO_WORKSPACE = "/path/to/workspace/";

    private final String projectName;
    private final List<IClasspathEntry> entries;

    private final String defaultOutputLocation = "/target/classes/";

    private IMarker marker;

    public static JavaProjectBuilder project()
    {
        return project("project");
    }

    public static JavaProjectBuilder project(String projectName)
    {
        return new JavaProjectBuilder(projectName);
    }

    private JavaProjectBuilder(String projectName)
    {
        this.projectName = projectName;
        entries = newArrayList();
    }

    public JavaProjectBuilder withJar(String jarName)
    {
        return addInternalJar(jarName, false);
    }

    public JavaProjectBuilder andProjectJar(String projectName, String jarName)
    {
        return addInternalJar(projectName, jarName, false);
    }

    public JavaProjectBuilder andExportedJar(String jarName)
    {
        return addInternalJar(jarName, true);
    }

    private JavaProjectBuilder addInternalJar(String jarProjectName, String jarName, boolean exported)
    {
        String path = "/" + jarProjectName + "/" + jarName + ".jar";
        return addJar(path, exported);
    }

    private JavaProjectBuilder addInternalJar(String jarName, boolean exported)
    {
        return addInternalJar(projectName, jarName, exported);
    }

    public JavaProjectBuilder withMarker(IMarker aMarker)
    {
        marker = aMarker;
        return this;
    }

    public JavaProjectBuilder andExtenralJar(String externalJarPath)
    {
        return addJar(externalJarPath, false);
    }

    private JavaProjectBuilder addJar(String path, boolean exported)
    {
        entries.add(new EntryBuilder().contentKind(K_BINARY).entryKind(CPE_LIBRARY).path(new Path(path))
                        .exported(exported).build());

        return this;
    }

    public JavaProjectBuilder andSourceDirectory(String sourceDirectory, String outputDirectory)
    {
        return andSourceDirectory(sourceDirectory, outputDirectory, false);
    }

    public IJavaProject andExportedSourceDirectory(String sourceDirectory, String outputDirectory)
    {
        return andSourceDirectory(sourceDirectory, outputDirectory, true);
    }

    public JavaProjectBuilder andSourceDirectory(String sourceDirectory, String outputDirectory, boolean exported)
    {
        Path sourcePath = projectPath(sourceDirectory);
        Path outputPath = projectPath(outputDirectory);

        entries.add(new EntryBuilder().contentKind(K_SOURCE).entryKind(CPE_SOURCE).path(sourcePath)
                        .outputPath(outputPath).exported(exported).build());

        return this;
    }

    public JavaProjectBuilder andSourceDirectory(String sourceDirectory)
    {
        Path sourcePath = projectPath(sourceDirectory);

        entries.add(new EntryBuilder().contentKind(K_SOURCE).entryKind(CPE_SOURCE).path(sourcePath).build());

        return this;
    }

    private Path projectPath(String path)
    {
        return new Path("/" + projectName + "/" + path);
    }

    public JavaProjectBuilder andDependsOn(String dependencyProjectName)
    {
        entries.add(new EntryBuilder().contentKind(K_SOURCE).entryKind(CPE_PROJECT)
                        .path(new Path("/" + dependencyProjectName)).build());

        return this;
    }

    // Implemented IJavaProject methods

    public IClasspathEntry[] getResolvedClasspath(boolean b)
    {
        return entries.toArray(new IClasspathEntry[entries.size()]);
    }

    public IResource getCorrespondingResource()
    {
        IResource resource = createNiceMock(IResource.class);
        expect(resource.getLocation()).andReturn(new Path(projectName)).anyTimes();
        expect(resource.getFullPath()).andReturn(getPath());
        replay(resource);
        return resource;
    }

    private void addMarker(IResource resource)
    {
        if (marker != null)
        {
            try
            {
                expect(resource.createMarker((String) anyObject())).andReturn(marker);
                resource.deleteMarkers((String) anyObject(), eq(true), eq(DEPTH_INFINITE));
            }
            catch (CoreException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public IPath getOutputLocation()
    {
        return new Path("/" + projectName + defaultOutputLocation);
    }

    public IPath getPath()
    {
        return new Path("/" + projectName);
    }

    public IResource getResource()
    {
        return getCorrespondingResource();
    }

    public IProject getProject()
    {
        try
        {
            IProject project = createMock(IProject.class);
            expect(project.getLocationURI()).andReturn(new URI("/root/" + projectName));
            expect(project.getLocation()).andReturn(new Path("/root/" + projectName));
            expect(project.getName()).andReturn(projectName);
            expect(project.findMaxProblemSeverity(JAVA_MODEL_PROBLEM_MARKER, false, DEPTH_INFINITE)).andReturn(-1);
            addMarker(project);
            replay(project);
            return project;
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getElementName()
    {
        // No idea if this is right
        return projectName;
    }

    public IClasspathEntry[] getRawClasspath()
    {
        return getResolvedClasspath(false);
    }

    // Below are the non-supported IProject interface classes. Nothing to see here.

    public IClasspathEntry decodeClasspathEntry(String s)
    {
        throw new UnsupportedOperationException();
    }

    public String encodeClasspathEntry(IClasspathEntry iClasspathEntry)
    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement findElement(IPath iPath)
    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement findElement(IPath iPath, WorkingCopyOwner workingCopyOwner)
    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement findElement(String s, WorkingCopyOwner workingCopyOwner)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragment findPackageFragment(IPath iPath)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragmentRoot findPackageFragmentRoot(IPath iPath)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry iClasspathEntry)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, WorkingCopyOwner workingCopyOwner)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, WorkingCopyOwner workingCopyOwner, IProgressMonitor iProgressMonitor)

    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, String s1)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, String s1, IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, String s1, WorkingCopyOwner workingCopyOwner)
    {
        throw new UnsupportedOperationException();
    }

    public IType findType(String s, String s1, WorkingCopyOwner workingCopyOwner, IProgressMonitor iProgressMonitor)

    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragmentRoot[] getAllPackageFragmentRoots()
    {
        throw new UnsupportedOperationException();
    }

    public Object[] getNonJavaResources()
    {
        throw new UnsupportedOperationException();
    }

    public String getOption(String s, boolean b)
    {
        throw new UnsupportedOperationException();
    }

    public Map<?, ?> getOptions(boolean b)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragmentRoot getPackageFragmentRoot(String s)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragmentRoot getPackageFragmentRoot(IResource iResource)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragmentRoot[] getPackageFragmentRoots()
    {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public IPackageFragmentRoot[] getPackageFragmentRoots(IClasspathEntry iClasspathEntry)
    {
        throw new UnsupportedOperationException();
    }

    public IPackageFragment[] getPackageFragments()
    {
        throw new UnsupportedOperationException();
    }

    public String[] getRequiredProjectNames()
    {
        throw new UnsupportedOperationException();
    }

    public boolean hasBuildState()
    {
        throw new UnsupportedOperationException();
    }

    public boolean hasClasspathCycle(IClasspathEntry[] iClasspathEntries)
    {
        throw new UnsupportedOperationException();
    }

    public boolean isOnClasspath(IJavaElement iJavaElement)
    {
        throw new UnsupportedOperationException();
    }

    public boolean isOnClasspath(IResource iResource)
    {
        throw new UnsupportedOperationException();
    }

    public IEvaluationContext newEvaluationContext()
    {
        throw new UnsupportedOperationException();
    }

    public ITypeHierarchy newTypeHierarchy(IRegion iRegion, IProgressMonitor iProgressMonitor)

    {
        throw new UnsupportedOperationException();
    }

    public ITypeHierarchy newTypeHierarchy(IRegion iRegion, WorkingCopyOwner workingCopyOwner,
                    IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public ITypeHierarchy newTypeHierarchy(IType iType, IRegion iRegion, IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public ITypeHierarchy newTypeHierarchy(IType iType, IRegion iRegion, WorkingCopyOwner workingCopyOwner,
                    IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public IPath readOutputLocation()
    {
        throw new UnsupportedOperationException();
    }

    public IClasspathEntry[] readRawClasspath()
    {
        throw new UnsupportedOperationException();
    }

    public void setOption(String s, String s1)
    {
        throw new UnsupportedOperationException();
    }

    public void setOptions(Map map)
    {
        throw new UnsupportedOperationException();
    }

    public void setOutputLocation(IPath iPath, IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public void setRawClasspath(IClasspathEntry[] iClasspathEntries, IPath iPath, boolean b,
                    IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public void setRawClasspath(IClasspathEntry[] iClasspathEntries, boolean b, IProgressMonitor iProgressMonitor)

    {
        throw new UnsupportedOperationException();
    }

    public void setRawClasspath(IClasspathEntry[] iClasspathEntries, IProgressMonitor iProgressMonitor)

    {
        throw new UnsupportedOperationException();
    }

    public void setRawClasspath(IClasspathEntry[] iClasspathEntries, IPath iPath, IProgressMonitor iProgressMonitor)

    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement[] getChildren()
    {
        throw new UnsupportedOperationException();
    }

    public boolean hasChildren()
    {
        throw new UnsupportedOperationException();
    }

    public boolean exists()
    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement getAncestor(int i)
    {
        throw new UnsupportedOperationException();
    }

    public String getAttachedJavadoc(IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public int getElementType()
    {
        throw new UnsupportedOperationException();
    }

    public String getHandleIdentifier()
    {
        throw new UnsupportedOperationException();
    }

    public IJavaModel getJavaModel()
    {
        throw new UnsupportedOperationException();
    }

    public IJavaProject getJavaProject()
    {
        throw new UnsupportedOperationException();
    }

    public IOpenable getOpenable()
    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement getParent()
    {
        throw new UnsupportedOperationException();
    }

    public IJavaElement getPrimaryElement()
    {
        throw new UnsupportedOperationException();
    }

    public ISchedulingRule getSchedulingRule()
    {
        throw new UnsupportedOperationException();
    }

    public IResource getUnderlyingResource()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isStructureKnown()
    {
        throw new UnsupportedOperationException();
    }

    public Object getAdapter(Class aClass)
    {
        throw new UnsupportedOperationException();
    }

    public void close()
    {
        throw new UnsupportedOperationException();
    }

    public String findRecommendedLineSeparator()
    {
        throw new UnsupportedOperationException();
    }

    public IBuffer getBuffer()
    {
        throw new UnsupportedOperationException();
    }

    public boolean hasUnsavedChanges()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isConsistent()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isOpen()
    {
        return true;
    }

    public void makeConsistent(IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public void open(IProgressMonitor iProgressMonitor)
    {
        throw new UnsupportedOperationException();
    }

    public void save(IProgressMonitor iProgressMonitor, boolean b)
    {
        throw new UnsupportedOperationException();
    }
}
