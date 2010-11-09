package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;

import java.io.File;
import java.util.Collections;

import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.changedetect.FakeChangeDetector;
import org.junit.Test;

public class WhenTestFileIsRemoved
{
    @Test
    public void shouldReloadIndex() throws Exception
    {
        InfinitestCore core = createCore(withRemovedFiles(), withNoTestsToRun());
        EventSupport eventSupport = new EventSupport();
        core.addTestQueueListener(eventSupport);
        core.update();
        eventSupport.assertReloadOccured();
    }

    private ChangeDetector withRemovedFiles()
    {
        return new FakeChangeDetector(Collections.<File> emptySet(), true);
    }
}
