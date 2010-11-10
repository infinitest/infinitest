package org.infinitest.eclipse;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.eclipse.console.ConsoleClearingListener;
import org.infinitest.eclipse.console.ConsoleOutputWriter;
import org.infinitest.eclipse.console.ConsolePopulatingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreLifecycleObserver implements CoreLifecycleListener
{
    private final ResultCollector collector;
    private final ConsoleClearingListener clearingListener;
    private final ConsolePopulatingListener populatingListener;
    private final SlowTestObserver slowTestObserver;

    @Autowired
    public CoreLifecycleObserver(ResultCollector collector, SlowTestObserver slowTestObserver)
    {
        this.collector = collector;
        this.slowTestObserver = slowTestObserver;
        ConsoleOutputWriter writer = new ConsoleOutputWriter();
        populatingListener = new ConsolePopulatingListener(writer);
        clearingListener = new ConsoleClearingListener(writer);
    }

    public void coreCreated(InfinitestCore core)
    {
        collector.attachCore(core);
        core.addTestResultsListener(slowTestObserver);
        core.addDisabledTestListener(slowTestObserver);
        core.addConsoleOutputListener(populatingListener);
        core.addTestQueueListener(clearingListener);
    }

    public void coreRemoved(InfinitestCore core)
    {
        collector.detachCore(core);
        core.removeConsoleOutputListener(populatingListener);
        core.removeTestQueueListener(clearingListener);
        core.removeTestResultsListener(slowTestObserver);
        core.removeDisabledTestListener(slowTestObserver);
    }
}
