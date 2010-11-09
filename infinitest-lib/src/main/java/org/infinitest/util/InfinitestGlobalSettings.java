package org.infinitest.util;

import static java.util.logging.Level.*;

import java.util.logging.Level;

/**
 * @author <a href="mailto:benrady@gmail.com"Ben Rady</a>
 */
public class InfinitestGlobalSettings
{
    private static Level logLevel = Level.INFO;
    private static long slowTestTimeLimit = 500;

    public static void resetToDefaults()
    {
        setLogLevel(INFO);
        setSlowTestTimeLimit(500);
    }

    public static Level getLogLevel()
    {
        return logLevel;
    }

    public static void setLogLevel(Level level)
    {
        logLevel = level;
    }

    public static void setSlowTestTimeLimit(long timeLimit)
    {
        slowTestTimeLimit = timeLimit;
    }

    public static long getSlowTestTimeLimit()
    {
        return slowTestTimeLimit;
    }
}
