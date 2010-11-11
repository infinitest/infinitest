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

import java.io.File;
import java.util.Collection;

import org.infinitest.testrunner.TestCaseEvent;
import org.infinitest.testrunner.TestResultsListener;

@SuppressWarnings("all")
public class FakeInfinitestCore implements InfinitestCore
{
    public void addTestQueueListener(TestQueueListener listener)
    {
    }

    public void removeTestQueueListener(TestQueueListener listener)
    {
    }

    public void addTestResultsListener(TestResultsListener listener)
    {
    }

    public void removeTestResultsListener(TestResultsListener listener)
    {
    }

    public int update()
    {
        return 0;
    }

    public void reload()
    {
    }

    public void setRuntimeEnvironment(RuntimeEnvironment environment)
    {
    }

    public String getName()
    {
        return null;
    }

    public void addConsoleOutputListener(ConsoleOutputListener listener)
    {
    }

    public void removeConsoleOutputListener(ConsoleOutputListener listener)
    {
    }

    public void addReloadListener(ReloadListener listener)
    {
    }

    public void addDisabledTestListener(DisabledTestListener listener)
    {
    }

    public void removeDisabledTestListener(DisabledTestListener anyObject)
    {
        throw new UnsupportedOperationException();
    }

    public RuntimeEnvironment getRuntimeEnvironment()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isEventSourceFor(TestCaseEvent testCaseEvent)
    {
        throw new UnsupportedOperationException();
    }

    public int update(Collection<File> changedFiles)
    {
        throw new UnsupportedOperationException();
    }
}
