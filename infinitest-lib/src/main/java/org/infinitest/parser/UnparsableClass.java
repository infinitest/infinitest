package org.infinitest.parser;

import static java.util.Collections.*;

import java.io.File;
import java.util.Collection;

public class UnparsableClass implements JavaClass
{
    private final String classname;

    public UnparsableClass(String classname)
    {
        this.classname = classname;
    }

    public File getClassFile()
    {
        return null;
    }

    public Collection<String> getImports()
    {
        return emptyList();
    }

    public String getName()
    {
        return classname;
    }

    public boolean isATest()
    {
        return false;
    }

    public boolean locatedInClassFile()
    {
        return false;
    }

    public void dispose()
    {
    }

}
