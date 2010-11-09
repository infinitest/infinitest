package org.infinitest;

import java.io.File;
import java.util.List;

public interface ClasspathProvider
{
    /**
     * List of class directories that Infinitest should monitor for changes.
     */
    List<File> getClassOutputDirs();

    /**
     * The classpath used to launch the test runner process. This will include the class output
     * directories. It will also include any supporting infinitest jars or classes.
     */
    String getCompleteClasspath();

    List<File> classDirectoriesInClasspath();
}