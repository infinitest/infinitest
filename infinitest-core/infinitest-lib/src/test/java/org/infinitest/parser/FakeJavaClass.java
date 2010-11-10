package org.infinitest.parser;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class FakeJavaClass extends AbstractJavaClass
{
    private final String name;

    public FakeJavaClass(String name)
    {
        this.name = name;
    }

    public Collection<String> getImports()
    {
        return Collections.emptyList();
    }

    public String getName()
    {
        return name;
    }

    public boolean isATest()
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL()
    {
        throw new UnsupportedOperationException();
    }

    public boolean locatedInClassFile()
    {
        throw new UnsupportedOperationException();
    }

    public File getClassFile()
    {
        throw new UnsupportedOperationException();
    }
}
