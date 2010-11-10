package org.infinitest;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Sets.*;
import static org.easymock.EasyMock.*;
import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.TestResultsListener;
import org.infinitest.testrunner.TestRunner;
import org.junit.Test;

import com.fakeco.fakeproduct.simple.PassingTest;

public class WhenTestsAreDisabled
{
    @SuppressWarnings("unchecked")
    @Test
    public void shouldFireAppropriateEvent()
    {
        TestRunner runner = createMock(TestRunner.class);
        runner.addTestResultsListener((TestResultsListener) anyObject());
        runner.setTestPriority((Comparator<String>) anyObject());
        TestDetector testDetector = createMock(TestDetector.class);
        expect(testDetector.getCurrentTests()).andReturn(setify("MyClass", "OtherClass"));
        expect(testDetector.getCurrentTests()).andReturn(setify("OtherClass"));
        Set<JavaClass> emptyClassSet = Collections.<JavaClass> emptySet();
        expect(testDetector.findTestsToRun((Collection<File>) anyObject())).andReturn(emptyClassSet);
        replay(runner, testDetector);

        DefaultInfinitestCore core = new DefaultInfinitestCore(runner, new ControlledEventQueue());
        core.setChangeDetector(withChangedFiles(PassingTest.class));
        core.setTestDetector(testDetector);
        final Set<String> disabledTestList = newHashSet();
        core.addDisabledTestListener(new DisabledTestListener()
        {
            public void testsDisabled(Collection<String> testName)
            {
                disabledTestList.addAll(testName);
            }
        });
        core.update();
        verify(testDetector);
        assertEquals("MyClass", getOnlyElement(disabledTestList));
    }
}
