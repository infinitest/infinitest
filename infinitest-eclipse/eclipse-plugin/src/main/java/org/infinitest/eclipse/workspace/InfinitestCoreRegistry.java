package org.infinitest.eclipse.workspace;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static org.infinitest.util.InfinitestUtils.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.infinitest.InfinitestCore;
import org.infinitest.eclipse.CoreLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class InfinitestCoreRegistry implements CoreRegistry
{
    private final Map<URI, InfinitestCore> coreMap = newHashMap();
    private final List<CoreLifecycleListener> listeners = newArrayList();

    @Autowired
    InfinitestCoreRegistry(CoreLifecycleListener... listeners)
    {
        for (CoreLifecycleListener each : listeners)
        {
            addLifecycleListener(each);
        }
    }

    public void addCore(URI projectUri, InfinitestCore core)
    {
        fireAddedEvent(core);
        coreMap.put(projectUri, core);
    }

    public InfinitestCore getCore(URI projectUri)
    {
        return coreMap.get(projectUri);
    }

    public void removeCore(URI projectUri)
    {
        InfinitestCore core = coreMap.remove(projectUri);
        if (core != null)
        {
            fireRemovedEvent(core);
            log("Removing core " + core.getName());
        }
    }

    private void fireRemovedEvent(InfinitestCore core)
    {
        for (CoreLifecycleListener each : listeners)
        {
            each.coreRemoved(core);
        }
    }

    private void fireAddedEvent(InfinitestCore core)
    {
        for (CoreLifecycleListener each : listeners)
        {
            each.coreCreated(core);
        }
    }

    public int indexedCoreCount()
    {
        return coreMap.size();
    }

    public void addLifecycleListener(CoreLifecycleListener listener)
    {
        listeners.add(listener);
    }
}
