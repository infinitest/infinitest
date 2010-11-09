package org.infinitest.intellij.plugin;

public class SourceNavigatorStub implements SourceNavigator
{

    public SourceNavigator open(String className)
    {
        return this;
    }

    public void line(int line)
    {
    }
}
