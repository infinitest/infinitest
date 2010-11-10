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
