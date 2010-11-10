package org.infinitest.util;

import static java.util.Arrays.*;

import java.io.File;
import java.util.List;

import org.infinitest.ClasspathProvider;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.StandaloneClasspath;

public class FakeEnvironments
{
    public static File fakeClassDirectory()
    {
        return new File("target/test-classes");
    }

    public static List<File> fakeBuildPaths()
    {
        return asList(new File("target/classes"), fakeClassDirectory());
    }

    public static ClasspathProvider fakeClasspath()
    {
        return new StandaloneClasspath(fakeBuildPaths(), systemClasspath());
    }

    public static RuntimeEnvironment fakeEnvironment()
    {
        return new RuntimeEnvironment(fakeBuildPaths(), fakeWorkingDirectory(), systemClasspath(), currentJavaHome());
    }

    public static File fakeWorkingDirectory()
    {
        return new File(".");
    }

    public static File currentJavaHome()
    {
        return new File(System.getProperty("java.home"));
    }

    public static ClasspathProvider emptyClasspath()
    {
        return new StandaloneClasspath(asList(new File("thisdirectorydoesnotexist")), "classpath");
    }

    public static RuntimeEnvironment emptyRuntimeEnvironment()
    {
        return new RuntimeEnvironment(asList(new File("thisdirectorydoesnotexist")), fakeWorkingDirectory(),
                        "classpath", currentJavaHome());
    }

    public static String systemClasspath()
    {
        // This is a workaround for the maven surefire plugin classpath issue listed here:
        // http://jira.codehaus.org/browse/SUREFIRE-435
        if (System.getProperty("surefire.test.class.path") != null)
        {
            return System.getProperty("surefire.test.class.path");
        }

        return System.getProperty("java.class.path");
    }
}
