package org.infinitest.eclipse.workspace;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

public interface ResourceFinder
{
    IProject getProject(URI projectUri);

    IResource findResourceForSourceFile(String sourceFile);

    IWorkspaceRoot workspaceRoot();

    List<IJavaProject> getJavaProjects();

    File findFileFor(IPath path);
}
