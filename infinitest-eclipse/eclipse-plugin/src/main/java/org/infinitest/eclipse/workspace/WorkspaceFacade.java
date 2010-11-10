package org.infinitest.eclipse.workspace;

import org.eclipse.core.runtime.CoreException;

public interface WorkspaceFacade
{
    void updateProjects() throws CoreException;
}