package org.infinitest.intellij;

import static java.lang.System.*;
import static java.util.Collections.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.infinitest.RuntimeEnvironment;

public class FakeModuleSettings implements ModuleSettings
{
    private final String name;

    public FakeModuleSettings(String name)
    {
        this.name = name;
    }

    public void writeToLogger(Logger log)
    {
        // nothing to do here
    }

    public String getName()
    {
        return name;
    }

    public List<File> listOutputDirectories()
    {
        return emptyList();
    }

    public String buildClasspathString()
    {
        return null;
    }

    public File getWorkingDirectory()
    {
        return new File(".");
    }

    public RuntimeEnvironment getRuntimeEnvironment()
    {
        return new RuntimeEnvironment(Collections.<File> emptyList(), new File("."), "",
            new File(getProperty("java.home")));
    }
}
