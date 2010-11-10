package org.infinitest.eclipse.workspace;

import static java.util.Arrays.*;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class WorkspaceResourceFinder implements ResourceFinder
{
    private ISourceContainer sourceContainer;
    private IWorkspace workspace;

    @Autowired
    WorkspaceResourceFinder(ISourceContainer sourceContainer)
    {
        this.sourceContainer = sourceContainer;
    }

    @Autowired
    public void setWorkspace(IWorkspace workspace)
    {
        this.workspace = workspace;
    }

    public IProject getProject(URI projectUri)
    {
        try
        {
            for (IResource each : workspaceRoot().members())
            {
                if (each.getLocationURI().equals(projectUri))
                    return (IProject) each;
            }
            throw new IllegalArgumentException("Could not find project for " + projectUri);
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }

    public IResource findResourceForSourceFile(String sourceFile)
    {
        // RISK Just taking the first resource here. What if we have the same class defined in
        // multiple places?
        try
        {
            Object[] elements = sourceContainer.findSourceElements(sourceFile);
            if (elements.length > 0)
                return (IResource) elements[0];
            return null;
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }

    public IWorkspaceRoot workspaceRoot()
    {
        return workspace.getRoot();
    }

    public List<IJavaProject> getJavaProjects()
    {
        IWorkspaceRoot root = workspaceRoot();
        IJavaModel javaModel = JavaCore.create(root);
        try
        {
            return asList(javaModel.getJavaProjects());
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException(e);
        }
    }

    public File findFileFor(IPath path)
    {
        IWorkspaceRoot root = workspaceRoot();
        IResource member = root.findMember(path);
        if (member == null)
            return null;
        File file = new File(member.getLocationURI().getPath());
        if (file.exists())
            return file;
        return null;
    }
}
