package org.infinitest.eclipse.event;

import static org.eclipse.core.resources.IResourceChangeEvent.*;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.workspace.WorkspaceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ClassFileChangeProcessor extends EclipseEventProcessor
{
    private final WorkspaceFacade workspace;

    @Autowired
    ClassFileChangeProcessor(WorkspaceFacade workspace)
    {
        super("Looking for tests");
        this.workspace = workspace;
    }

    @Override
    public boolean canProcessEvent(IResourceChangeEvent event)
    {
        return (event.getType() & (POST_BUILD | POST_CHANGE)) > 0;
    }

    @Override
    public void processEvent(IResourceChangeEvent event) throws CoreException
    {
        if (containsClassFileChanges(getDeltas(event)))
        {
            workspace.updateProjects();
        }
    }

    private boolean containsClassFileChanges(IResourceDelta... deltas)
    {
        // DEBT SHould use IResourceDeltaVisitor instead
        for (IResourceDelta delta : deltas)
        {
            if (isClassFile(delta) || containsClassFileChanges(delta.getAffectedChildren()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isClassFile(IResourceDelta delta)
    {
        return delta.getFullPath().toPortableString().endsWith(".class");
    }
}
