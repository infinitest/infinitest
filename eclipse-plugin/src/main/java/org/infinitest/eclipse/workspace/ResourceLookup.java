package org.infinitest.eclipse.workspace;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;

public interface ResourceLookup
{
    // RISK We don't handle duplicate classes well
    // There are a lot of places where this is called and the first resource is used.
    // We probably need a better strategy for resolving situations where there are many versions
    // of the same class in the project
    List<IResource> findResourcesForClassName(String className);

    IWorkspaceRoot workspaceRoot();
}
