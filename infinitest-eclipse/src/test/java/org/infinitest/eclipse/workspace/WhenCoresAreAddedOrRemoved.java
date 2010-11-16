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
package org.infinitest.eclipse.workspace;

import static org.easymock.EasyMock.*;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.TestQueueListener;
import org.infinitest.eclipse.CoreLifecycleObserver;
import org.infinitest.eclipse.SlowTestObserver;
import org.infinitest.eclipse.console.ConsoleClearingListener;
import org.infinitest.eclipse.console.ConsolePopulatingListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class WhenCoresAreAddedOrRemoved
{
    private InfinitestCore core;
    private CoreLifecycleObserver observer;

    @Before
    public void inContext()
    {
        SlowTestObserver mock = Mockito.mock(SlowTestObserver.class);
        observer = new CoreLifecycleObserver(new ResultCollector(), mock);
        core = createMock(InfinitestCore.class);
    }

    @Test
    public void shouldAttachAndDetachListenersFromCore()
    {
        core.addTestResultsListener(isA(ResultCollector.class));
        core.addTestQueueListener(isA(TestQueueListener.class));
        core.addDisabledTestListener(isA(ResultCollector.class));
        core.addConsoleOutputListener(isA(ConsolePopulatingListener.class));
        core.addTestQueueListener(isA(ConsoleClearingListener.class));
        core.addTestResultsListener(isA(SlowTestObserver.class));
        core.addDisabledTestListener(isA(SlowTestObserver.class));

        core.removeTestResultsListener(isA(ResultCollector.class));
        core.removeTestQueueListener(isA(TestQueueListener.class));
        core.removeDisabledTestListener(isA(ResultCollector.class));
        core.removeConsoleOutputListener(isA(ConsolePopulatingListener.class));
        core.removeTestResultsListener(isA(SlowTestObserver.class));
        core.removeDisabledTestListener(isA(SlowTestObserver.class));
        core.removeTestQueueListener(isA(ConsoleClearingListener.class));
        replay(core);

        observer.coreCreated(core);
        observer.coreRemoved(core);
        verify(core);
    }
}
