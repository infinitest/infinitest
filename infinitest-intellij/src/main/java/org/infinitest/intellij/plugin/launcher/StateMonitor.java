package org.infinitest.intellij.plugin.launcher;

import static java.lang.System.*;
import static org.infinitest.CoreStatus.*;

import org.infinitest.CoreStatus;
import org.infinitest.StatusChangeListener;

class StateMonitor implements StatusChangeListener
{
    private long cycleStart;
    private boolean statusInFlux;

    public StateMonitor()
    {
        cycleStart = getCurrentTime();
    }

    public long getCycleLengthInMillis()
    {
        return getCurrentTime() - cycleStart;
    }

    public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        if (newStatus.equals(RUNNING))
        {
            statusInFlux = true;
        }
        if (statusInFlux && newStatus.equals(PASSING))
        {
            cycleStart = getCurrentTime();
            statusInFlux = false;
        }
    }

    protected long getCurrentTime()
    {
        return currentTimeMillis();
    }
}
