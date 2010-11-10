package org.infinitest.util;

import static com.google.common.collect.Maps.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.infinitest.util.InfinitestUtils.*;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenLoggingMessagesAndErrors implements LoggingListener
{
    private Map<String, Throwable> errors;
    private Map<String, Level> messages;
    private Level oldLevel;

    @Before
    public void inContext()
    {
        errors = newHashMap();
        messages = newHashMap();
        addLoggingListener(this);
        oldLevel = getLogLevel();
    }

    @After
    public void cleanup()
    {
        setLogLevel(oldLevel);
    }

    @Test
    public void shouldFireErrorEventToAllowForAlternateLoggingMethods()
    {
        String message = "an error occured";
        RuntimeException error = new RuntimeException();
        log(message, error);
        assertEquals(error, errors.get(message));
    }

    @Test
    public void shouldFireErrorEventWithoutThrowable()
    {
        String message = "an error occured";
        log(Level.SEVERE, message);
        assertEquals(Level.SEVERE, messages.get(message));
    }

    @Test
    public void shouldFireInfoEvent()
    {
        String message = "an error occured";
        log(message);
        assertEquals(Level.INFO, messages.get(message));
    }

    @Test
    public void canControlLogLevel()
    {
        setLogLevel(Level.OFF);
        String message = "an error occured";
        log(Level.SEVERE, message);
        assertNull(messages.get(message));
    }

    public void logError(String message, Throwable throwable)
    {
        errors.put(message, throwable);
    }

    public void logMessage(Level level, String logMsg)
    {
        messages.put(logMsg, level);
    }
}
