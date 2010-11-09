package org.infinitest.testrunner;

import java.io.Serializable;

public class MethodStats implements Serializable
{
    public long startTime;
    public long stopTime;
    public final String methodName;

    public MethodStats(String methodName)
    {
        this.methodName = methodName;
    }

    public long duration()
    {
        return stopTime - startTime;
    }
}
