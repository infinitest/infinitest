package org.infinitest.eclipse.status;

import static com.google.common.collect.Lists.*;

import java.util.List;

public class StatusEventSupport
{
    private List<StatusEventListener> listeners = newArrayList();

    public void addListener(StatusEventListener listener)
    {
        listeners.add(listener);
    }

    public void fireStatusChange(WorkspaceStatus status)
    {
        for (StatusEventListener each : listeners)
            each.statusChanged(status);
    }
}
