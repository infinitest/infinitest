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
package org.infinitest.parser;

import static com.google.common.collect.Lists.*;
import static org.hamcrest.Matchers.*;
import static org.infinitest.util.FakeEnvironments.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.infinitest.changedetect.FileChangeDetector;
import org.junit.Before;
import org.junit.Test;

public class LargeWorkspacePerformanceSimulation
{
    private static final int PROJECT_COUNT = 25;
    private static final int UPDATE_COUNT = 10;
    private final boolean showOutput;
    private List<File> files;
    private List<ClassFileIndex> indexes;

    public LargeWorkspacePerformanceSimulation()
    {
        // Don't delete this or JUnit will be angry
        this(false);
    }

    private LargeWorkspacePerformanceSimulation(boolean showOutput)
    {
        this.showOutput = showOutput;
    }

    @Before
    public void inContext() throws IOException
    {
        FileChangeDetector detector = new FileChangeDetector();
        detector.setClasspathProvider(fakeClasspath());
        indexes = newArrayList();
        files = newArrayList(detector.findChangedFiles());
    }

    public static void main(String[] args) throws IOException
    {
        // At last count, we could do this in 22 seconds on a 2.4ghz MacBookPro.
        // Although, it looks like when you run it in the test it runs a little slower
        LargeWorkspacePerformanceSimulation testHarness = new LargeWorkspacePerformanceSimulation(true);
        testHarness.inContext();
        System.out.println("File Count: " + testHarness.files.size());
        System.out.println("Max Memory " + humanReadable(Runtime.getRuntime().maxMemory()));

        long timestamp = System.currentTimeMillis();
        System.out.println("Creating Projects...");
        testHarness.createProjects();
        System.out.println("Created in " + (System.currentTimeMillis() - timestamp) + "ms");
        System.out.println("Updating Projects...");

        timestamp = System.currentTimeMillis();
        testHarness.updateProjects();
        System.out.println();
        long time = System.currentTimeMillis() - timestamp;
        System.out.println("Updated " + PROJECT_COUNT + " projects " + UPDATE_COUNT + " times in " + time + "ms");
    }

    @Test
    public void canScaleTo25ProjectsOfAtLeast250ClassesEach()
    {
        assertThat(files.size(), greaterThan(250));
        long timestamp = System.currentTimeMillis();
        createProjects();
        // We should be able to do this in 35 seconds on a 2.4ghz MBP. 60 sec is just for test
        // consistency
        assertThat(System.currentTimeMillis() - timestamp, lessThan(300000L));

        timestamp = System.currentTimeMillis();
        updateProjects();
        // We should be able to do this in 3 seconds on a 2.4ghz MBP. 10 sec is just for test
        // consistency
        assertThat(System.currentTimeMillis() - timestamp, lessThan(10000L));
    }

    private void updateProjects()
    {
        for (int updateIndex = 1; updateIndex <= UPDATE_COUNT; updateIndex++)
        {
            println("Pass " + updateIndex);
            simulateUpdates(files, indexes);
            println("");
        }
    }

    private void createProjects()
    {
        for (int projectIndex = 0; projectIndex < PROJECT_COUNT; projectIndex++)
        {
            print(".");
            indexes.add(simulateNewProject());
        }
    }

    private ClassFileIndex simulateNewProject()
    {
        ClassFileIndex index = new ClassFileIndex(fakeClasspath());
        index.findClasses(files);
        return index;
    }

    private void simulateUpdates(List<File> files, List<ClassFileIndex> indexes)
    {
        int filesChanges = 0;
        for (ClassFileIndex each : indexes)
        {
            Set<JavaClass> classes = each.findClasses(files.subList(0, filesChanges++ % 10));
            each.findChangedParents(classes);
            print(".");
        }
    }

    private void print(String string)
    {
        if (showOutput)
        {
            System.out.print(string);
        }
    }

    private void println(String string)
    {
        if (showOutput)
        {
            System.out.println(string);
        }
    }

    private static String humanReadable(long memory)
    {
        return memory / 1024 + "k";
    }

}
