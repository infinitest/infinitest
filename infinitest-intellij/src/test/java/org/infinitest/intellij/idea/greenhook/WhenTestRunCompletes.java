package org.infinitest.intellij.idea.greenhook;

import static org.infinitest.CoreStatus.*;
import static org.mockito.Mockito.*;

import org.infinitest.intellij.plugin.greenhook.GreenHook;
import org.infinitest.intellij.plugin.greenhook.GreenHookListener;
import org.junit.Before;
import org.junit.Test;

public class WhenTestRunCompletes
{
    private GreenHookListener listener;
    private GreenHook hook;

    @Before
    public void inContext()
    {
        listener = new GreenHookListener();
        hook = mock(GreenHook.class);
        listener.add(hook);
    }

    @Test
    public void shouldRunGreenHooksIfPassing()
    {
        listener.coreStatusChanged(PASSING, PASSING);
        verify(hook).execute();
    }

    @Test
    public void shouldNotRunGreenHooksIfFailing()
    {
        listener.coreStatusChanged(PASSING, FAILING);
        verify(hook, never()).execute();
    }

    @Test
    public void shouldNotThrowExceptionIfNoGreenHooksAdded()
    {
        listener = new GreenHookListener();
        listener.coreStatusChanged(PASSING, PASSING);
        verify(hook, never()).execute();
    }
}
