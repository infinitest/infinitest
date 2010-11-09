package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.changedetect.FakeChangeDetector;
import org.junit.Before;
import org.junit.Test;

public class WhenAnErrorOccurs
{
    private ChangeDetector failingDetector;
    protected boolean shouldFail;
    private InfinitestCore core;

    @Before
    public void inContext()
    {
        shouldFail = true;
        failingDetector = new FakeChangeDetector()
        {
            @Override
            public Set<File> findChangedFiles() throws IOException
            {
                if (shouldFail)
                    throw new IOException();
                return Collections.emptySet();
            }
        };
        core = createCore(failingDetector, withNoTestsToRun());
    }

    @Test
    public void shouldReloadIndexOnIOException() throws Exception
    {
        EventSupport statusSupport = new EventSupport();
        core.addTestQueueListener(statusSupport);
        core.update();
        shouldFail = false;
        core.update();
        shouldFail = true;
        core.update();
        statusSupport.assertReloadOccured();
    }

    @Test(expected = FatalInfinitestError.class)
    public void shouldNotRetryIfErrorOccursTwiceInARow()
    {
        core.update();
        core.update();
    }
}
