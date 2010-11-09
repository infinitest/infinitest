package org.infinitest.util;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Multimaps.*;
import static java.lang.System.*;
import static java.util.Arrays.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.infinitest.ControlledEventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.filter.TestFilter;

import com.google.common.collect.Multimap;

public class InfinitestPerformanceHarness
{
    private static final Multimap<String, Long> perfRecords = newArrayListMultimap();
    private static final Map<String, Long> lastStart = newHashMap();

    public static void main(String[] args)
    {
        System.out.println("Starting");
        File workingDir = new File(".");
        List<File> outputDirs = asList(new File("target/classes", "target/test-classes"));
        String classpath = System.getProperty("java.class.path");
        File javaHome = new File(System.getProperty("java.home"));
        RuntimeEnvironment environment = new RuntimeEnvironment(outputDirs, workingDir, classpath, javaHome);
        InfinitestCoreBuilder builder = new InfinitestCoreBuilder(environment, new ControlledEventQueue());
        builder.setFilter(new TestFilter()
        {
            public boolean match(String className)
            {
                return true;
            }

            public void updateFilterList()
            {
                // nothing to do here
            }
        });
        InfinitestCore core = builder.createCore();
        core.update();
        JOptionPane.showMessageDialog(null, "Start Profiler");
        System.out.println("Updating");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++)
        {
            core.update();
            core.reload();
            System.out.println(i);
        }
        System.out.println("Finished in " + (System.currentTimeMillis() - start));

        // JOptSimple ~16 sec for 25 updates

        printResults();
        JOptionPane.showMessageDialog(null, "Stop Profiler");
    }

    public static void start(String string)
    {
        lastStart.put(string, currentTimeMillis());
    }

    public static void stop(String string)
    {
        perfRecords.put(string, System.currentTimeMillis() - lastStart.get(string));
    }

    private static void printResults()
    {
        for (String key : perfRecords.keySet())
        {
            long totalTime = 0;
            for (Long timestamp : perfRecords.get(key))
                totalTime += timestamp;
            System.err.println(key + " : " + totalTime + "ms");
        }
    }

}
