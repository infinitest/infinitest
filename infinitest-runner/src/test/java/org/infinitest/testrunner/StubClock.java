package org.infinitest.testrunner;

public class StubClock implements Clock
{
    public long time;

    public long currentTimeMillis()
    {
        return time;
    }
}
