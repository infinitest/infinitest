package org.infinitest.eclipse.event;

import static java.util.logging.Level.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.infinitest.util.InfinitestUtils.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaModelException;
import org.infinitest.eclipse.workspace.CoreRegistry;
import org.infinitest.eclipse.workspace.EclipseProject;
import org.infinitest.eclipse.workspace.ProjectSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class CleanEventProcessor extends EclipseEventProcessor
{
    private final CoreRegistry coreRegistry;
    private final ProjectSet projectSet;

    @Autowired
    CleanEventProcessor(CoreRegistry coreRegistry, ProjectSet projectSet)
    {
        super("Clearing Infinitest Indexes");
        this.coreRegistry = coreRegistry;
        this.projectSet = projectSet;
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws JavaModelException
    {
        cleanProjects(getDeltas(event));
    }

    private void cleanProjects(IResourceDelta[] projectResourceDeltas)
    {
        for (IResourceDelta projectDelta : projectResourceDeltas)
        {
            cleanProject(projectDelta);
        }
    }

    private void cleanProject(IResourceDelta projectResourceDelta)
    {
        IPath projectPath = projectResourceDelta.getResource().getFullPath();
        EclipseProject project = projectSet.findProject(projectPath);
        if (project == null)
        {
            log(WARNING, "Could not find project for resource " + projectPath);
        }
        else
        {
            coreRegistry.removeCore(project.getLocationURI());
        }
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        return event.getBuildKind() == CLEAN_BUILD && (event.getType() & POST_BUILD) > 0;
    }

}
