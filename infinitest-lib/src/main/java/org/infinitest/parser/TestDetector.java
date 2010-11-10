package org.infinitest.parser;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.infinitest.ClasspathProvider;

public interface TestDetector
{
    void clear();

    Set<JavaClass> findTestsToRun(Collection<File> changedFiles);

    boolean isEmpty();

    void setClasspathProvider(ClasspathProvider classpath);

    Set<String> getCurrentTests();
}