package org.infinitest.eclipse;

import org.infinitest.InfinitestCore;

public interface CoreLifecycleListener
{
    void coreCreated(InfinitestCore core);

    void coreRemoved(InfinitestCore core);
}
