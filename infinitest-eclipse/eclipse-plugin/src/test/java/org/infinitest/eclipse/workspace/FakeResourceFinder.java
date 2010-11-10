package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static org.easymock.EasyMock.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

public class FakeResourceFinder implements ResourceFinder, ResourceLookup
{
    private final List<IJavaProject> projects;

    public FakeResourceFinder(List<IJavaProject> projects)
    {
        this.projects = projects;
    }

    public FakeResourceFinder(IJavaProject... projects)
    {
        this(newArrayList(projects));
    }

    public IResource findResourceForSourceFile(String sourceFile)
    {
        return createNiceMock(IResource.class);
    }

    public IProject getProject(URI projectUri)
    {
        return project(projectUri.getPath()).getProject();
    }

    public IProject findProjectResourceForClassName(String className)
    {
        return null;
    }

    public IResource findResourceFromClassName(String className)
    {
        return null;
    }

    public IWorkspaceRoot workspaceRoot()
    {
        return createNiceMock(IWorkspaceRoot.class);
    }

    public File findFileFor(IPath path)
    {
        return null;
    }

    public List<IJavaProject> getJavaProjects()
    {
        return projects;
    }

    public List<IResource> findResourcesForClassName(String className)
    {
        return emptyList();
    }
}
