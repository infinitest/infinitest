package org.infinitest.intellij;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class WhenDiscoveringInfinitestJars
{
    @Test
    public void shouldDetermineFileNamesFromEmbeddedPom()
    {
        InfinitestJarLocator locator = new InfinitestJarLocator();
        List<String> jarNames = locator.findInfinitestJarNames();
        assertThat(jarNames, hasItem("infinitest-core-5.0.52-SNAPSHOT.jar"));
        assertThat(jarNames, hasItem("infinitest-runner-5.0.52-SNAPSHOT.jar"));
    }

    @Test
    public void shouldDetermineInfinitestVersionFromEmbeddedPom() {
        InfinitestJarLocator locator = new InfinitestJarLocator();
        assertThat(locator.findInfinitestVersion(), is("5.0.52-SNAPSHOT"));
    }
}
