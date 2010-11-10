package org.infinitest.testrunner;

import java.io.Serializable;

public class MethodStats implements Serializable
{
    private static final long serialVersionUID = -8853619641593524214L;

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
