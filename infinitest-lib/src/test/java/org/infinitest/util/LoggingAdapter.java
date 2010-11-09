package org.infinitest.util;

import static com.google.common.collect.Maps.*;

import java.util.Map;
import java.util.logging.Level;

public class LoggingAdapter implements LoggingListener
{
    private final Map<String, Level> messages = newLinkedHashMap();

    @SuppressWarnings("all")
    public void logError(String message, Throwable throwable)
    {
    }

    public void logMessage(Level level, String message)
    {
        messages.put(message, level);
    }

    public boolean hasMessage(String message, Level level)
    {
        return level.equals(messages.get(message));
    }

    @Override
    public String toString()
    {
        return messages.toString();
    }
}
