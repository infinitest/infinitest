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
