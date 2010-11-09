package org.infinitest.intellij;

import org.infinitest.intellij.plugin.SourceNavigator;

public class FakeSourceNavigator implements SourceNavigator
{
    private String className;
    private int line;

    public SourceNavigator open(String className)
    {
        this.className = className;
        return this;
    }

    public void line(int line)
    {
        this.line = line;
    }

    public String getClassName()
    {
        return className;
    }

    public int getLine()
    {
        return line;
    }
}
