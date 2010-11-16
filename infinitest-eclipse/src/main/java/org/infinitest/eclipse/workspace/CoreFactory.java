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

import java.net.URI;

import org.infinitest.ConcurrencyController;
import org.infinitest.EventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.MultiCoreConcurrencyController;
import org.infinitest.RuntimeEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class CoreFactory implements CoreSettings
{
    private final EventQueue eventQueue;
    private final ConcurrencyController concurrencyController;

    @Autowired
    public CoreFactory(EventQueue eventQueue)
    {
        this.eventQueue = eventQueue;
        this.concurrencyController = new MultiCoreConcurrencyController();
    }

    public InfinitestCore createCore(URI projectUri, String projectName, RuntimeEnvironment environment)
    {
        InfinitestCore core;
        InfinitestCoreBuilder coreBuilder = new InfinitestCoreBuilder(environment, eventQueue);
        coreBuilder.setUpdateSemaphore(concurrencyController);
        coreBuilder.setName(projectName);
        core = coreBuilder.createCore();
        return core;
    }

    public void setConcurrentCoreCount(int coreCount)
    {
        concurrencyController.setCoreCount(coreCount);
    }
}
