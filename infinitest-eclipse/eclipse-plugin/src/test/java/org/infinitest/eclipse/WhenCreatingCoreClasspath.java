package org.infinitest.eclipse;

import static java.util.Collections.*;
import static org.easymock.EasyMock.*;
import static org.infinitest.eclipse.InfinitestCoreClasspath.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class WhenCreatingCoreClasspath
{
    private InfinitestPlugin plugin;

    @Before
    public void inContext()
    {
        Bundle bundle = createNiceMock(Bundle.class);
        List<URL> urls = Arrays.asList(getClass().getResource("WhenCreatingCoreClasspath.class"));
        expect(bundle.findEntries("", "*infinitest-runner*.jar", true)).andReturn(enumeration(urls));
        expect(bundle.findEntries("", "*infinitest-runner*.jar", true)).andReturn(enumeration(urls));
        expectLastCall();
        replay(bundle);
        plugin = new InfinitestPlugin();
        plugin.setPluginBundle(bundle);
    }

    @Test
    public void shouldWriteInfinitestCoreOutToTempDirectory()
    {
        File coreJarLocation = getCoreJarLocation(plugin);
        assertTrue(coreJarLocation.exists());
        assertTrue(coreJarLocation.getAbsolutePath().endsWith(".jar"));
    }

    @Test
    public void shouldRecreateJarIfItIsDeleted()
    {
        File coreJarLocation = getCoreJarLocation(plugin);
        assertTrue(coreJarLocation.exists());
        assertTrue(coreJarLocation.getAbsolutePath().endsWith(".jar"));
        assertTrue(coreJarLocation.delete());

        coreJarLocation = getCoreJarLocation(plugin);
        assertTrue(coreJarLocation.exists());
    }
}
