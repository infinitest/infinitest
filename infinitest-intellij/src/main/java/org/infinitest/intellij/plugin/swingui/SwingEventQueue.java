package org.infinitest.intellij.plugin.swingui;

import static javax.swing.SwingUtilities.*;

import java.lang.reflect.InvocationTargetException;

import org.infinitest.EventQueue;
import org.infinitest.NamedRunnable;

public class SwingEventQueue implements EventQueue
{
    public void push(Runnable runnable)
    {
        invokeLater(runnable);
    }

    public void pushAndWait(Runnable runnable)
    {
        try
        {
            if (!isEventDispatchThread())
            {
                invokeAndWait(runnable);
            }
            else
            {
                runnable.run();
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void pushNamed(NamedRunnable runnable)
    {
        push(runnable);
    }
}
