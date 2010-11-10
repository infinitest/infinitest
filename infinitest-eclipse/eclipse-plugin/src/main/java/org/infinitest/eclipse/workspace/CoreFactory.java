package org.infinitest.eclipse.workspace;

import java.net.URI;

import org.infinitest.ConcurrencyController;
import org.infinitest.EventQueue;
import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.MultiCoreConcurrencyController;
import org.infinitest.RuntimeEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class CoreFactory implements CoreSettings
{
    private final EventQueue eventQueue;
    private final ConcurrencyController concurrencyController;

    @Autowired
    public CoreFactory(EventQueue eventQueue)
    {
        this.eventQueue = eventQueue;
        this.concurrencyController = new MultiCoreConcurrencyController();
    }

    public InfinitestCore createCore(URI projectUri, String projectName, RuntimeEnvironment environment)
    {
        InfinitestCore core;
        InfinitestCoreBuilder coreBuilder = new InfinitestCoreBuilder(environment, eventQueue);
        coreBuilder.setUpdateSemaphore(concurrencyController);
        coreBuilder.setName(projectName);
        core = coreBuilder.createCore();
        return core;
    }

    public void setConcurrentCoreCount(int coreCount)
    {
        concurrencyController.setCoreCount(coreCount);
    }
}
