package org.infinitest.eclipse.event;

import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IResourceChangeEvent.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.*;
import static org.junit.Assert.*;

import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.runtime.CoreException;
import org.infinitest.eclipse.ResourceEventSupport;
import org.infinitest.eclipse.workspace.WorkspaceFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenRespondingToBuildEvents extends ResourceEventSupport
{
    private ClassFileChangeProcessor processor;
    private WorkspaceFacade workspace;

    @Before
    public void inContext()
    {
        workspace = createStrictMock(WorkspaceFacade.class);
        processor = new ClassFileChangeProcessor(workspace);
    }

    @After
    public void verifyWorkspace()
    {
        verify(workspace);
    }

    @Test
    public void shouldNotRespondToPreBuildEvents()
    {
        replay(workspace);
        IResourceChangeEvent event = new ResourceChangeEvent(this, PRE_BUILD, AUTO_BUILD, null);
        assertFalse(processor.canProcessEvent(event));
    }

    @Test
    public void shouldNotUpdateIfClassesAreNotChanged() throws CoreException
    {
        replay(workspace);
        processor.processEvent(emptyEvent());
    }

    @Test
    public void shouldRespondToPostBuildEvents()
    {
        replay(workspace);
        IResourceChangeEvent event = new ResourceChangeEvent(this, POST_BUILD, AUTO_BUILD, null);
        assertTrue(processor.canProcessEvent(event));
    }

    @Test
    public void shouldRespondToPostChangeEvents()
    {
        replay(workspace);
        IResourceChangeEvent event = new ResourceChangeEvent(this, POST_CHANGE, AUTO_BUILD, null);
        assertTrue(processor.canProcessEvent(event));
    }
}
