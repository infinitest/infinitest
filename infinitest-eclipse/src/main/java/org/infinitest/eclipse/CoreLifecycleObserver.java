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
package org.infinitest.eclipse;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.eclipse.console.ConsoleClearingListener;
import org.infinitest.eclipse.console.ConsoleOutputWriter;
import org.infinitest.eclipse.console.ConsolePopulatingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreLifecycleObserver implements CoreLifecycleListener
{
    private final ResultCollector collector;
    private final ConsoleClearingListener clearingListener;
    private final ConsolePopulatingListener populatingListener;
    private final SlowTestObserver slowTestObserver;

    @Autowired
    public CoreLifecycleObserver(ResultCollector collector, SlowTestObserver slowTestObserver)
    {
        this.collector = collector;
        this.slowTestObserver = slowTestObserver;
        ConsoleOutputWriter writer = new ConsoleOutputWriter();
        populatingListener = new ConsolePopulatingListener(writer);
        clearingListener = new ConsoleClearingListener(writer);
    }

    public void coreCreated(InfinitestCore core)
    {
        collector.attachCore(core);
        core.addTestResultsListener(slowTestObserver);
        core.addDisabledTestListener(slowTestObserver);
        core.addConsoleOutputListener(populatingListener);
        core.addTestQueueListener(clearingListener);
    }

    public void coreRemoved(InfinitestCore core)
    {
        collector.detachCore(core);
        core.removeConsoleOutputListener(populatingListener);
        core.removeTestQueueListener(clearingListener);
        core.removeTestResultsListener(slowTestObserver);
        core.removeDisabledTestListener(slowTestObserver);
    }
}
