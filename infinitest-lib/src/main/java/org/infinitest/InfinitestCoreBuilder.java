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

import org.infinitest.changedetect.FileChangeDetector;
import org.infinitest.filter.RegexFileFilter;
import org.infinitest.filter.TestFilter;
import org.infinitest.filter.TestNGConfigurator;
import org.infinitest.parser.ClassFileTestDetector;
import org.infinitest.parser.TestDetector;
import org.infinitest.testrunner.MultiProcessRunner;
import org.infinitest.testrunner.TestRunner;

/**
 * Used to create instances of an {@link InfinitestCore}.
 * 
 * @author bjrady
 */
public class InfinitestCoreBuilder
{
    private TestFilter filterList;
    private final Class<? extends TestRunner> runnerClass;
    private final RuntimeEnvironment runtimeEnvironment;
    private final EventQueue eventQueue;
    private String coreName = "";
    private ConcurrencyController controller;
    private final TestNGConfigurator testNGConfigurator;

    public InfinitestCoreBuilder(RuntimeEnvironment environment, EventQueue eventQueue)
    {
        runtimeEnvironment = environment;
        this.eventQueue = eventQueue;
        File filterFile = getFilterFile(environment);
        filterList = new RegexFileFilter(filterFile);
        testNGConfigurator = new TestNGConfigurator(filterFile);
        runnerClass = MultiProcessRunner.class;
        controller = new SingleLockConcurrencyController();
    }

    /**
     * Creates a new core from the existing builder settings.
     */
    public InfinitestCore createCore()
    {
        TestRunner runner = createRunner();
        runner.setConcurrencyController(controller);
        DefaultInfinitestCore core = new DefaultInfinitestCore(runner, eventQueue);
        core.setName(coreName);
        core.setChangeDetector(new FileChangeDetector());
        core.setTestDetector(createTestDetector(filterList));
        core.addTestQueueListener(testNGConfigurator);
        core.setRuntimeEnvironment(runtimeEnvironment);
        return core;
    }

    protected TestDetector createTestDetector(TestFilter testFilterList)
    {
        return new ClassFileTestDetector(testFilterList);
    }

    /**
     * Sets a test filter. The default filter uses a list of regular expressions extracted from a
     * file in the project working directory called infinitest.filters
     */
    public void setFilter(TestFilter testFilter)
    {
        filterList = testFilter;
    }

    private TestRunner createRunner()
    {
        try
        {
            return runnerClass.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Cannot create runner from class " + runnerClass
                            + ". Did you provide a no-arg constructor?", e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Cannot access runner class " + runnerClass, e);
        }
    }

    public void setName(String coreName)
    {
        this.coreName = coreName;
    }

    public void setUpdateSemaphore(ConcurrencyController semaphore)
    {
        controller = semaphore;
    }

    public static File getFilterFile(RuntimeEnvironment environment)
    {
        final String filterFileLocation = environment.getWorkingDirectory().getAbsolutePath() + File.separator
                        + "infinitest.filters";
        File filterFile = new File(filterFileLocation);
        return filterFile;
    }
}
