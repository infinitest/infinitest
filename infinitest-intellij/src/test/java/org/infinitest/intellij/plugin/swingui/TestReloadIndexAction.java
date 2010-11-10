package org.infinitest.intellij.plugin.swingui;

import static org.mockito.Mockito.*;

import org.infinitest.InfinitestCore;
import org.junit.Test;

public class TestReloadIndexAction
{
    @Test
    public void shouldReloadWhenActionPerformed()
    {
        final InfinitestCore reloader = mock(InfinitestCore.class);

        ReloadIndexAction action = new ReloadIndexAction(reloader);
        action.actionPerformed(null);

        verify(reloader).reload();
    }
}
