package org.infinitest.intellij;

import static java.lang.String.*;

import java.util.logging.Level;

import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.util.LoggingListener;

public class InfinitestLoggingListener implements LoggingListener
{
    private InfinitestView view;

    public InfinitestLoggingListener(InfinitestView view)
    {
        this.view = view;
    }

    public void logError(String message, Throwable throwable)
    {
        view.writeError(message);
    }

    public void logMessage(Level level, String message)
    {
        view.writeLogMessage(leftAlign(level) + " " + message);
    }

    private String leftAlign(Level info)
    {
        return format("%-10s", info.getName());
    }
}
