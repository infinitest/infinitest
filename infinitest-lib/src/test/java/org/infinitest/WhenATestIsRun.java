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
import static org.junit.Assert.*;

import java.util.Comparator;

import org.infinitest.testrunner.TestResultsListener;
import org.infinitest.testrunner.TestRunner;
import org.junit.Test;

public class WhenATestIsRun
{
    @Test
    public void shouldEvent()
    {
        EventNormalizer normalizer = new EventNormalizer(new ControlledEventQueue());
        assertNotNull(normalizer.consoleEventNormalizer(new ConsoleListenerAdapter()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFireEventsForConsoleUpdates()
    {
        TestRunner runner = createMock(TestRunner.class);
        runner.addTestResultsListener((TestResultsListener) anyObject());
        runner.setTestPriority((Comparator<String>) anyObject());
        runner.addConsoleOutputListener((ConsoleOutputListener) anyObject());
        runner.removeConsoleOutputListener((ConsoleOutputListener) anyObject());

        replay(runner);
        DefaultInfinitestCore core = new DefaultInfinitestCore(runner, new ControlledEventQueue());
        ConsoleListenerAdapter listener = new ConsoleListenerAdapter();
        core.addConsoleOutputListener(listener);
        core.removeConsoleOutputListener(listener);
        verify(runner);
    }
}
