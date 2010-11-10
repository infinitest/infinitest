package org.infinitest;

import java.io.File;

public class JavaHomeException extends RuntimeException
{
    private static final long serialVersionUID = -1L;

    JavaHomeException(File javaHome)
    {
        super("Could not find java executable at " + javaHome.getAbsolutePath());
    }
}
