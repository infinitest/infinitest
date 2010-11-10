package org.infinitest.intellij.plugin.swingui;

import org.infinitest.InfinitestCore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
