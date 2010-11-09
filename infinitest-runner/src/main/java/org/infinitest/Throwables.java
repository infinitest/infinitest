package org.infinitest;

import junit.framework.AssertionFailedError;

public abstract class Throwables
{

    public static boolean isTestFailure(Throwable exception)
    {
        return exception instanceof AssertionFailedError || exception instanceof AssertionError;
    }

}
