package org.infinitest;

import java.io.File;

import org.infinitest.changedetect.FileChangeDetector;
import org.infinitest.filter.RegexFileFilter;
import org.infinitest.filter.TestFilter;
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
    private Class<? extends TestRunner> runnerClass;
    private final RuntimeEnvironment runtimeEnvironment;
    private EventQueue eventQueue;
    private String coreName = "";
    private ConcurrencyController controller;

    public InfinitestCoreBuilder(RuntimeEnvironment environment, EventQueue eventQueue)
    {
        runtimeEnvironment = environment;
        this.eventQueue = eventQueue;
        String filterFileLocation = environment.getWorkingDirectory().getAbsolutePath() + File.separator
                        + "infinitest.filters";
        this.filterList = new RegexFileFilter(new File(filterFileLocation));
        this.runnerClass = MultiProcessRunner.class;
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
        this.filterList = testFilter;
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
        this.controller = semaphore;
    }
}
