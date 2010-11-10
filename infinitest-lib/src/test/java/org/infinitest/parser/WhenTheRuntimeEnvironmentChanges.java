package org.infinitest.parser;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.fakeco.fakeproduct.id.FakeId;

public class WhenTheRuntimeEnvironmentChanges extends DependencyGraphTestBase
{
    @Test
    public void shouldRecreateTheClassFileIndex()
    {
        addToDependencyGraph(FakeId.class);
        assertEquals(1, getGraph().getIndexedClasses().size());

        getGraph().setClasspathProvider(emptyClasspath());
        assertEquals(0, getGraph().getIndexedClasses().size());
    }
}
