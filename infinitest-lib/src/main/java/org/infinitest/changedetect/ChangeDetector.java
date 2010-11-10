package org.infinitest.changedetect;


import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.infinitest.ClasspathProvider;

public interface ChangeDetector
{
    Set<File> findChangedFiles() throws IOException;

    void clear();
    

    boolean filesWereRemoved();

    void setClasspathProvider(ClasspathProvider classpath);
}