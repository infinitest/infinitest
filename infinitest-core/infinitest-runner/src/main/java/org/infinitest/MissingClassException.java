package org.infinitest;

import java.io.File;

public class MissingClassException extends NoClassDefFoundError
{
    private static final long serialVersionUID = -1L;

    public MissingClassException(File file)
    {
        super("Expected class file at " + file.getAbsolutePath());
    }

    public MissingClassException(String msg, Throwable e)
    {
        super(msg + " " + e.getMessage());
    }

    public MissingClassException(String missingClass)
    {
        super(missingClass);
    }
}
