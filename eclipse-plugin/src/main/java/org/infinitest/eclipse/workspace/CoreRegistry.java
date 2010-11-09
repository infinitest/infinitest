package org.infinitest.eclipse.workspace;

import java.net.URI;

import org.infinitest.InfinitestCore;
import org.infinitest.eclipse.CoreLifecycleListener;

public interface CoreRegistry
{
    void addCore(URI projectUri, InfinitestCore core);

    InfinitestCore getCore(URI projectUri);

    void removeCore(URI projectUri);

    void addLifecycleListener(CoreLifecycleListener listener);

}