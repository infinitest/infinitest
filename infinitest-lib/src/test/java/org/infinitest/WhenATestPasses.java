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
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenATestPasses
{
    @Test
    public void shouldFireSuccessEvents()
    {
        ControlledEventQueue eventQueue = new ControlledEventQueue();
        DefaultInfinitestCore core = createCore(withChangedFiles(), withTests(PASSING_TEST), eventQueue);
        ResultCollector collector = new ResultCollector(core);
        EventSupport testStatus = new EventSupport();
        core.addTestResultsListener(testStatus);

        core.update();
        eventQueue.flush();

        testStatus.assertTestsStarted(PASSING_TEST);
        testStatus.assertTestPassed(PASSING_TEST);
        assertFalse(collector.hasFailures());
    }
}
