package org.infinitest;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.infinitest.parser.FakeJavaClass;
import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestRunner;
import org.junit.Before;
import org.junit.Test;

public class WhenTriggeringACoreUpdate
{
    private List<File> updatedFiles;
    private DefaultInfinitestCore core;
    private TestDetector testDetector;

    @Before
    public void inContext()
    {
        updatedFiles = newArrayList();
        core = new DefaultInfinitestCore(createMock(TestRunner.class), new ControlledEventQueue());
        testDetector = createMock(TestDetector.class);
        expect(testDetector.getCurrentTests()).andReturn(Collections.<String> emptySet()).times(2);
        core.setTestDetector(testDetector);
    }

    private void testsToExpect(JavaClass... tests)
    {
        expect(testDetector.findTestsToRun(updatedFiles)).andReturn(newHashSet(tests));
    }

    @Test
    public void canUseAKnownListOfChangedFilesToReduceFileSystemAccess()
    {
        testsToExpect();
        replay(testDetector);
        core.update(updatedFiles);
        verify(testDetector);
    }

    @Test
    public void shouldReturnTheNumberOfTestsRun()
    {
        testsToExpect(new FakeJavaClass("FakeTest"));
        replay(testDetector);

        assertEquals(1, core.update(updatedFiles));
    }
}
