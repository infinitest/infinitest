package org.infinitest.eclipse;

import static java.util.logging.Level.*;
import static org.infinitest.eclipse.InfinitestPlugin.*;

import java.util.logging.Level;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.infinitest.util.LoggingListener;

public class EclipseLoggingListener implements LoggingListener
{
    public void logError(String message, Throwable throwable)
    {
        Status status = new Status(IStatus.ERROR, PLUGIN_ID, message, throwable);
        InfinitestPlugin plugin = InfinitestPlugin.getInstance();
        if (plugin != null)
            plugin.getLog().log(status);
    }

    public void logMessage(Level level, String message)
    {
        Status status = new Status(levelToStatus(level), PLUGIN_ID, message);
        InfinitestPlugin plugin = InfinitestPlugin.getInstance();
        if (plugin != null)
            plugin.getLog().log(status);
    }

    private int levelToStatus(Level level)
    {
        if (level.equals(SEVERE))
            return IStatus.ERROR;
        if (level.equals(WARNING))
            return IStatus.WARNING;
        return IStatus.INFO;
    }
}
