package org.infinitest;

import static org.easymock.EasyMock.*;
import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.util.Comparator;

import org.infinitest.changedetect.ChangeDetector;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestResultsListener;
import org.infinitest.testrunner.TestRunner;
import org.junit.Test;

public class WhenTheRuntimeEnvironmentChanges
{
    @Test
    public void shouldTriggerACompleteReloadInTheCore() throws Exception
    {
        InfinitestCore core = createCore(withNoChangedFiles(), withNoTestsToRun());
        EventSupport eventSupport = new EventSupport();
        core.addTestQueueListener(eventSupport);
        core.setRuntimeEnvironment(fakeEnvironment());
        eventSupport.assertReloadOccured();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUpdateSupportingClassesInTheCore()
    {
        RuntimeEnvironment environment = fakeEnvironment();
        TestRunner testRunner = createMock(TestRunner.class);
        testRunner.setRuntimeEnvironment(environment);
        testRunner.addTestResultsListener((TestResultsListener) anyObject());
        testRunner.setTestPriority((Comparator<String>) anyObject());

        TestDetector testDetector = createMock(TestDetector.class);
        testDetector.clear();
        testDetector.setClasspathProvider(environment);

        ChangeDetector changeDetector = createMock(ChangeDetector.class);
        changeDetector.setClasspathProvider(environment);
        changeDetector.clear();
        replay(testRunner, testDetector, changeDetector);

        DefaultInfinitestCore core = new DefaultInfinitestCore(testRunner, new FakeEventQueue());
        core.setTestDetector(testDetector);
        core.setChangeDetector(changeDetector);
        core.setRuntimeEnvironment(environment);

        verify(testDetector, testDetector, changeDetector);
    }

    @Test
    public void shouldDoNothingIfEnvironmentIsNotActuallyDifferent() throws Exception
    {
        InfinitestCore core = createCore(withNoChangedFiles(), withNoTestsToRun());
        EventSupport eventSupport = new EventSupport();
        core.addTestQueueListener(eventSupport);
        core.setRuntimeEnvironment(emptyRuntimeEnvironment());
        eventSupport.assertReloadOccured();
        assertEquals(1, eventSupport.getReloadCount());

        core.setRuntimeEnvironment(emptyRuntimeEnvironment());
        assertEquals(1, eventSupport.getReloadCount());
    }
}
