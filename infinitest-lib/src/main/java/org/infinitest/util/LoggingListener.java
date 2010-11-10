package org.infinitest.util;

import java.util.logging.Level;

public interface LoggingListener
{
    void logError(String message, Throwable throwable);

    void logMessage(Level level, String message);
}
