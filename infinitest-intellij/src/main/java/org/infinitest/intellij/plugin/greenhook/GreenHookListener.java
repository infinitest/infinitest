package org.infinitest.intellij.plugin.greenhook;

import static org.infinitest.CoreStatus.*;

import org.infinitest.CoreStatus;
import org.infinitest.StatusChangeListener;

public class GreenHookListener implements StatusChangeListener
{
    private GreenHook hook = new NullGreenHook();

    public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus)
    {
        if (PASSING.equals(newStatus))
            hook.execute();
    }

    public void add(GreenHook hook)
    {
        this.hook = hook;
    }
}
