package org.infinitest;

import static java.io.File.*;
import static java.util.Arrays.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class WhenSearchingForClassFilesToIndex
{
    @Test
    public void shouldSearchClassDirectoriesOnTheClasspath()
    {
        File outputDir = new File("target/classes");
        List<File> outputDirs = asList(outputDir);
        String classpath = "target/classes" + pathSeparator + "target/test-classes";
        RuntimeEnvironment environment = new RuntimeEnvironment(outputDirs, new File("."), classpath, new File(
                        "javahome"));
        List<File> directoriesInClasspath = environment.classDirectoriesInClasspath();
        assertThat(directoriesInClasspath, hasItems(new File("target/test-classes"), outputDir));
    }
}
