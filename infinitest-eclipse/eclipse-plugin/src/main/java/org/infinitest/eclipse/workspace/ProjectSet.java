package org.infinitest.eclipse.workspace;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;

public interface ProjectSet
{
    ProjectFacade findProject(IPath path);

    List<File> outputDirectories(EclipseProject project) throws JavaModelException;

    List<ProjectFacade> projects();

    boolean hasErrors() throws CoreException;
}