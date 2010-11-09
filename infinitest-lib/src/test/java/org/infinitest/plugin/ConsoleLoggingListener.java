package org.infinitest.plugin;

import java.util.logging.Level;

import org.infinitest.util.LoggingListener;

public class ConsoleLoggingListener implements LoggingListener
{
    public void logError(String message, Throwable throwable)
    {
        System.out.println(message);
        throwable.printStackTrace();
    }

    public void logMessage(Level level, String message)
    {
        System.out.println(message);
    }
}
