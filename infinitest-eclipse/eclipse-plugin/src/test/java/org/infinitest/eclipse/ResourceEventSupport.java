package org.infinitest.eclipse;

import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.infinitest.eclipse.workspace.JavaProjectBuilder.*;

import java.net.URI;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.Path;
import org.infinitest.eclipse.workspace.JavaProjectBuilder;

public class ResourceEventSupport
{
    protected static final String PROJECT_A_NAME = "/projectA";
    protected JavaProjectBuilder project = project(PROJECT_A_NAME);

    protected ResourceChangeEvent autoBuildEvent()
    {
        return buildEventWith(createDelta());
    }

    protected ResourceChangeEvent buildEventWith(IResourceDelta createDelta)
    {
        return new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, createDelta);
    }

    protected ResourceChangeEvent cleanBuildEvent()
    {
        return new ResourceChangeEvent(this, POST_BUILD, CLEAN_BUILD, createDelta());
    }

    protected ResourceChangeEvent emptyEvent()
    {
        return new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, createEmptyDelta());
    }

    protected IResourceDelta createDelta()
    {
        return createDelta(project);
    }

    protected URI projectAUri()
    {
        return project.getProject().getLocationURI();
    }

    protected IResourceDelta createEmptyDelta()
    {
        return createResourceDelta(project, new IResourceDelta[] {});
    }

    protected IResourceDelta createDelta(JavaProjectBuilder project)
    {
        IResourceDelta classResource = createMock(IResourceDelta.class);
        expect(classResource.getFullPath()).andReturn(new Path("a.class"));

        return createResourceDelta(project, new IResourceDelta[] { classResource });
    }

    protected IResourceDelta createResourceDelta(JavaProjectBuilder project, IResourceDelta... resourceDeltas)
    {
        IResourceDelta projectDelta = createMock(IResourceDelta.class);
        expect(projectDelta.getResource()).andReturn(project.getResource());
        expect(projectDelta.getFullPath()).andReturn(new Path("/workspace/projectA"));
        expect(projectDelta.getAffectedChildren()).andReturn(resourceDeltas);

        IResourceDelta delta = createMock(IResourceDelta.class);
        expect(delta.getAffectedChildren()).andReturn(new IResourceDelta[] { projectDelta });
        replay(delta, projectDelta);
        for (IResourceDelta each : resourceDeltas)
        {
            replay(each);
        }
        return delta;
    }
}
