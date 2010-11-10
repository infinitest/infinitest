package org.infinitest.eclipse.markers;

import static com.google.common.collect.Lists.*;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.infinitest.eclipse.workspace.ResourceFinder;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceLookupAdapter implements ResourceLookup
{
    private final ResourceFinder finder;

    @Autowired
    public ResourceLookupAdapter(ResourceFinder finder)
    {
        this.finder = finder;
    }

    public List<IResource> findResourcesForClassName(String className)
    {
        List<IResource> resources = newArrayList();
        for (String sourceFile : possibleFilenamesOf(className))
        {
            IResource resource = finder.findResourceForSourceFile(sourceFile);
            if (resource != null)
            {
                resources.add(resource);
            }
        }
        return resources;
    }

    private Iterable<String> possibleFilenamesOf(String className)
    {
        // Add .groovy, .scala?
        // Maybe this should happen in the resource finder?
        return newArrayList(className.replace(".", "/") + ".java");
    }

    public IWorkspaceRoot workspaceRoot()
    {
        return finder.workspaceRoot();
    }

}
