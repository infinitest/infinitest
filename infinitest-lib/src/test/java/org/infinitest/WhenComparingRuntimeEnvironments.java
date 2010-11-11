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

import static com.google.common.collect.Lists.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

public class WhenComparingRuntimeEnvironments
{
    @Test
    public void shouldCompareEqualEnvironments()
    {
        assertEquals(createEqualInstance(), createEqualInstance());
        assertEquals(createEqualInstance().hashCode(), createEqualInstance().hashCode());
    }

    @Test
    public void shouldCompareOutputDirectories()
    {
        RuntimeEnvironment env = createEnv("notTheSameOutputDir", "workingDir", "classpath", "javahome");
        assertThat(createEqualInstance(), not(equalTo(env)));
        assertThat(createEqualInstance().hashCode(), not(equalTo(env.hashCode())));
    }

    @Test
    public void shouldCompareWorkingDirectory()
    {
        RuntimeEnvironment env = createEnv("outputDir", "notTheSameWorkingDir", "classpath", "javahome");
        assertThat(createEqualInstance(), not(equalTo(env)));
    }

    @Test
    public void shouldCompareClasspath()
    {
        RuntimeEnvironment env = createEnv("outputDir", "workingDir", "notTheSameClasspath", "javahome");
        assertThat(createEqualInstance(), not(equalTo(env)));
    }

    @Test
    public void shouldCompareJavaHome()
    {
        RuntimeEnvironment env = createEnv("outputDir", "workingDir", "classpath", "notTheSameJavahome");
        assertThat(createEqualInstance(), not(equalTo(env)));
    }

    @Test
    public void shouldCompareAdditionalArgs()
    {
        RuntimeEnvironment env = createEqualInstance();
        env.addVMArgs(Arrays.asList("additionalArg"));
        assertThat(createEqualInstance(), not(equalTo(env)));
    }

    @Test
    public void shouldNotBeEqualToNull()
    {
        assertFalse(createEqualInstance().equals(null));
    }

    private RuntimeEnvironment createEnv(String outputDir, String workingDir, String classpath, String javahome)
    {
        RuntimeEnvironment env = new RuntimeEnvironment(newArrayList(new File(outputDir)), new File(workingDir),
                        classpath, new File(javahome));
        return env;
    }

    private RuntimeEnvironment createEqualInstance()
    {
        return createEnv("outputDir", "workingDir", "classpath", "javahome");
    }
}
