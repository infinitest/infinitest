package org.infinitest.util;

import static java.util.logging.Level.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class WhenConfiguringGlobalSettings
{
    @Test
    public void canResetToDefaults()
    {
        InfinitestGlobalSettings.setSlowTestTimeLimit(100);
        setLogLevel(CONFIG);
        resetToDefaults();
        assertEquals(INFO, getLogLevel());
        assertEquals(500, InfinitestGlobalSettings.getSlowTestTimeLimit());
    }
}
