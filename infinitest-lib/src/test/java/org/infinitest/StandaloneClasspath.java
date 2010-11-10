package org.infinitest;

import static org.infinitest.util.FakeEnvironments.*;

import java.io.File;
import java.util.List;

public class StandaloneClasspath implements ClasspathProvider
{
    private final List<File> classDirs;
    private final String classpath;
    private final List<File> classDirsInClasspath;

    public StandaloneClasspath(List<File> classOutputDirs, String classpath)
    {
        this(classOutputDirs, classOutputDirs, classpath);
    }

    public StandaloneClasspath(List<File> classOutputDirs, List<File> classDirsInClasspath, String classpath)
    {
        this.classDirs = classOutputDirs;
        this.classDirsInClasspath = classDirsInClasspath;
        this.classpath = classpath;
    }

    public StandaloneClasspath(List<File> classOutputDirs)
    {
        this(classOutputDirs, systemClasspath());
    }

    public List<File> getClassOutputDirs()
    {
        return classDirs;
    }

    public String getCompleteClasspath()
    {
        return classpath;
    }

    @Override
    public String toString()
    {
        return "Classpath :[" + classpath + "]  Class Directories: [" + classDirs + "]";
    }

    public List<File> classDirectoriesInClasspath()
    {
        return classDirsInClasspath;
    }

    public String getSystemClasspath()
    {
        return systemClasspath();
    }

}
