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
