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
package org.infinitest.plugin;

import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import org.infinitest.EventSupport;
import org.infinitest.FakeEventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.filter.TestFilter;
import org.infinitest.parser.TestDetector;
import org.junit.Before;
import org.junit.Test;

public class WhenConfiguringBuilder
{
    protected InfinitestCoreBuilder builder;
    private EventSupport eventSupport;
    private TestFilter filterUsedToCreateCore;

    @Before
    public final void mustProvideRuntimeEnvironmentAndEventQueue()
    {
        RuntimeEnvironment environment = new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(),
                        systemClasspath(), currentJavaHome());
        builder = new InfinitestCoreBuilder(environment, new FakeEventQueue());
        builder.setFilter(new InfinitestTestFilter());
    }

    @Test
    public void canUseCustomFilterToRemoveTestsFromTestRun()
    {
        TestFilter testFilter = new InfinitestTestFilter();
        builder = new InfinitestCoreBuilder(fakeEnvironment(), new FakeEventQueue())
        {

            @Override
            protected TestDetector createTestDetector(TestFilter testFilter)
            {
                filterUsedToCreateCore = testFilter;
                return super.createTestDetector(testFilter);
            }
        };
        builder.setFilter(testFilter);
        builder.createCore();
        assertSame(filterUsedToCreateCore, testFilter);
    }

    @Test
    public void canSetCoreName()
    {
        builder.setName("myCoreName");
        InfinitestCore core = builder.createCore();
        assertEquals("myCoreName", core.getName());
    }

    @Test
    public void shouldUseBlankCoreNameByDefault()
    {
        InfinitestCore core = builder.createCore();
        assertEquals("", core.getName());
    }

    @Test
    public void shouldSetRuntimeEnvironment()
    {
        InfinitestCore core = builder.createCore();
        assertEquals(fakeEnvironment(), core.getRuntimeEnvironment());
    }

    protected EventSupport createEventSupport(InfinitestCore core)
    {
        EventSupport event = new EventSupport(5000);
        core.addTestQueueListener(event);
        core.addTestResultsListener(event);
        return event;
    }

    protected void runTests() throws InterruptedException
    {
        InfinitestCore core = builder.createCore();
        eventSupport = createEventSupport(core);
        core.update();
        eventSupport.assertRunComplete();
    }
}
