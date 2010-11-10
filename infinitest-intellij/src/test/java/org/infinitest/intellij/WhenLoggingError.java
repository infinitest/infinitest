package org.infinitest.intellij;

import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.junit.Test;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WhenLoggingError
{
    private static final Exception ERROR = new Exception("test");

    @Test
    public void shouldDisplayErrorInView()
    {
        InfinitestView view = mock(InfinitestView.class);

        InfinitestLoggingListener listener = new InfinitestLoggingListener(view);
        listener.logError("test message", ERROR);

        verify(view).writeError(contains("test message"));
    }
}
