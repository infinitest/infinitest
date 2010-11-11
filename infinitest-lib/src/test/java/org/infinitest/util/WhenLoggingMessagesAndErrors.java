/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
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
