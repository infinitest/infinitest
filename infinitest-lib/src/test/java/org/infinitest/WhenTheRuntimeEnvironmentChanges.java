/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest;

import static org.infinitest.CoreDependencySupport.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
        TestRunner testRunner = mock(TestRunner.class);
        TestDetector testDetector = mock(TestDetector.class);
        ChangeDetector changeDetector = mock(ChangeDetector.class);

        DefaultInfinitestCore core = new DefaultInfinitestCore(testRunner, new FakeEventQueue());
        core.setTestDetector(testDetector);
        core.setChangeDetector(changeDetector);
        core.setRuntimeEnvironment(environment);

        verify(testRunner).setRuntimeEnvironment(environment);
        verify(testRunner).addTestResultsListener(any(TestResultsListener.class));
        verify(testRunner).setTestPriority(any(Comparator.class));
        verify(testDetector).clear();
        verify(testDetector).setClasspathProvider(environment);
        verify(changeDetector).setClasspathProvider(environment);
        verify(changeDetector).clear();
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
