package org.infinitest.eclipse.workspace;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class FakeProjectSet implements ProjectSet
{
    private final IJavaProject project;

    public FakeProjectSet(IJavaProject project)
    {
        this.project = project;
    }

    public ProjectFacade findProject(IPath path)
    {
        if (project.getPath().equals(path))
            return new ProjectFacade(project);
        throw new UnsupportedOperationException();
    }

    public boolean hasErrors() throws CoreException
    {
        throw new UnsupportedOperationException();
    }

    public List<ProjectFacade> projects()
    {
        throw new UnsupportedOperationException();
    }

    public List<File> outputDirectories(EclipseProject project) throws JavaModelException
    {
        throw new UnsupportedOperationException();
    }

}
