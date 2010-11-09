package org.infinitest;

public abstract class NamedRunnable implements Runnable
{
    private final String name;

    public NamedRunnable(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
