package org.infinitest;

import static javax.swing.SwingUtilities.*;

/**
 * use {@link ControlledEventQueue} for async updates
 */
public class FakeEventQueue implements EventQueue
{
    public void push(Runnable runnable)
    {
        invokeLater(runnable);
    }

    public void pushNamed(NamedRunnable runnable)
    {
        invokeLater(runnable);
    }
}
