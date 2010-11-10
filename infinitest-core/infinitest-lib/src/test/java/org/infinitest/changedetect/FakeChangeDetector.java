package org.infinitest.changedetect;

import static java.util.Collections.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.infinitest.ClasspathProvider;

public class FakeChangeDetector implements ChangeDetector
{
    private Set<File> changedFiles;
    private boolean filesRemoved;

    public FakeChangeDetector(Set<File> changedFiles, boolean filesRemoved)
    {
        this.changedFiles = changedFiles;
        this.filesRemoved = filesRemoved;
    }

    public FakeChangeDetector()
    {
        changedFiles = emptySet();
    }

    public void clear()
    {
        // nothing to do here
    }

    public boolean filesWereRemoved()
    {
        return filesRemoved;
    }

    /**
     * @throws IOException
     */
    public Set<File> findChangedFiles() throws IOException
    {
        changedFiles = Collections.emptySet();
        return changedFiles;
    }

    public void setClasspathProvider(ClasspathProvider classpath)
    {
        // nothing to do here
    }
}
