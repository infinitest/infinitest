package org.infinitest.testrunner;

class SystemClock implements Clock
{
    public long currentTimeMillis()
    {
        return System.currentTimeMillis();
    }
}
