package org.infinitest;

import static java.util.Collections.*;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.infinitest.parser.JavaClass;
import org.infinitest.parser.TestDetector;

class StubTestDetector implements TestDetector
{
    private boolean cleared;

    public void clear()
    {
        cleared = true;
    }

    public boolean isCleared()
    {
        return cleared;
    }

    public Set<JavaClass> findTestsToRun(Collection<File> changedFiles)
    {
        return emptySet();
    }

    public boolean isEmpty()
    {
        throw new UnsupportedOperationException();
    }

    public Set<String> getIndexedClasses()
    {
        throw new UnsupportedOperationException();
    }

    public void setClasspathProvider(ClasspathProvider classpath)
    {
        throw new UnsupportedOperationException();
    }

    public Set<String> getCurrentTests()
    {
        return emptySet();
    }
}