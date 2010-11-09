package org.infinitest.eclipse.workspace;

import static org.easymock.EasyMock.*;

import org.infinitest.InfinitestCore;
import org.infinitest.ResultCollector;
import org.infinitest.TestQueueListener;
import org.infinitest.eclipse.CoreLifecycleObserver;
import org.infinitest.eclipse.SlowTestObserver;
import org.infinitest.eclipse.console.ConsoleClearingListener;
import org.infinitest.eclipse.console.ConsolePopulatingListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class WhenCoresAreAddedOrRemoved
{
    private InfinitestCore core;
    private CoreLifecycleObserver observer;

    @Before
    public void inContext()
    {
        SlowTestObserver mock = Mockito.mock(SlowTestObserver.class);
        observer = new CoreLifecycleObserver(new ResultCollector(), mock);
        core = createMock(InfinitestCore.class);
    }

    @Test
    public void shouldAttachAndDetachListenersFromCore()
    {
        core.addTestResultsListener(isA(ResultCollector.class));
        core.addTestQueueListener(isA(TestQueueListener.class));
        core.addDisabledTestListener(isA(ResultCollector.class));
        core.addConsoleOutputListener(isA(ConsolePopulatingListener.class));
        core.addTestQueueListener(isA(ConsoleClearingListener.class));
        core.addTestResultsListener(isA(SlowTestObserver.class));
        core.addDisabledTestListener(isA(SlowTestObserver.class));

        core.removeTestResultsListener(isA(ResultCollector.class));
        core.removeTestQueueListener(isA(TestQueueListener.class));
        core.removeDisabledTestListener(isA(ResultCollector.class));
        core.removeConsoleOutputListener(isA(ConsolePopulatingListener.class));
        core.removeTestResultsListener(isA(SlowTestObserver.class));
        core.removeDisabledTestListener(isA(SlowTestObserver.class));
        core.removeTestQueueListener(isA(ConsoleClearingListener.class));
        replay(core);

        observer.coreCreated(core);
        observer.coreRemoved(core);
        verify(core);
    }
}
