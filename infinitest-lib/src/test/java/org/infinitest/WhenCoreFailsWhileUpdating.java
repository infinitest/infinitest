package org.infinitest;

import static org.easymock.EasyMock.*;
import static org.infinitest.CoreDependencySupport.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.infinitest.changedetect.ChangeDetector;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class WhenCoreFailsWhileUpdating
{
    Mockery context = new Mockery();
    private InfinitestCore core;
    private StubTestDetector testDetector;
    private ChangeDetector changeDetector;

    @Before
    public void inContext() throws IOException
    {
        changeDetector = createMock(ChangeDetector.class);
        expect(changeDetector.filesWereRemoved()).andReturn(false);
        expect(changeDetector.findChangedFiles()).andThrow(new IOException());
        changeDetector.clear();
        replay(changeDetector);
        testDetector = new StubTestDetector();
        core = createCore(changeDetector, testDetector);
    }

    @Test
    public void shouldIgnoreFailureAndReloadIndex()
    {
        core.update();
        assertTrue(testDetector.isCleared());
        verify(changeDetector);
    }
}
