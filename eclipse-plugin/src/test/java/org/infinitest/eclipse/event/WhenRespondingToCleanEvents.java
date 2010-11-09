package org.infinitest.eclipse.event;

import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.junit.Assert.*;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.jdt.core.JavaModelException;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.workspace.CoreRegistry;
import org.infinitest.eclipse.workspace.FakeProjectSet;
import org.infinitest.eclipse.workspace.ProjectSet;
import org.junit.Before;
import org.junit.Test;

public class WhenRespondingToCleanEvents extends ResourceEventSupport
{
    private CleanEventProcessor processor;
    private CoreRegistry coreRegistry;

    @Before
    public void inContext()
    {
        coreRegistry = createMock(CoreRegistry.class);
        processor = new CleanEventProcessor(coreRegistry, createMock(ProjectSet.class));
    }

    @Test
    public void shouldRemoveCoreOnACleanBuild() throws JavaModelException
    {
        processor = new CleanEventProcessor(coreRegistry, new FakeProjectSet(project));
        coreRegistry.removeCore(eq(projectAUri()));
        replay(coreRegistry);

        processEvent(cleanBuildEvent());
        verify(coreRegistry);
    }

    @Test
    public void shouldHandleACleanBuildOnAnUnIndexedProject() throws JavaModelException
    {
        replay(coreRegistry);

        processEvent(cleanBuildEvent());
        verify(coreRegistry);
    }

    @Test
    public void shouldOnlyRespondToPostBuildEvents()
    {
        IResourceChangeEvent event = new ResourceChangeEvent(this, POST_CHANGE, CLEAN_BUILD, null);
        assertFalse(processor.canProcessEvent(event));
    }

    private void processEvent(ResourceChangeEvent event) throws JavaModelException
    {
        assertTrue(processor.canProcessEvent(event));
        processor.processEvent(event);
    }
}
