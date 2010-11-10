package org.infinitest.eclipse.workspace;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.infinitest.InfinitestCore;
import org.infinitest.eclipse.CoreLifecycleListener;
import org.junit.Before;
import org.junit.Test;

public class WhenCreatingAndRemovingCores implements CoreLifecycleListener
{
    private InfinitestCoreRegistry registry;
    private InfinitestCore coreAdded;
    private InfinitestCore coreRemoved;

    @Before
    public void inContext()
    {
        registry = new InfinitestCoreRegistry();
    }

    @Test
    public void shouldTolerateRemovingACoreThatsNotThere() throws URISyntaxException
    {
        registry.removeCore(new URI("//thisIsNotAProject"));
    }

    @Test
    public void shouldFireEventWhenCoresAreCreatedOrRemoved() throws URISyntaxException
    {
        InfinitestCore mockCore = createNiceMock(InfinitestCore.class);
        registry.addLifecycleListener(this);
        registry.addCore(new URI("//someProject"), mockCore);
        assertSame(coreAdded, mockCore);

        registry.removeCore(new URI("//someProject"));
        assertSame(coreRemoved, mockCore);
    }

    public void coreCreated(InfinitestCore core)
    {
        coreAdded = core;
    }

    public void coreRemoved(InfinitestCore core)
    {
        coreRemoved = core;
    }
}
