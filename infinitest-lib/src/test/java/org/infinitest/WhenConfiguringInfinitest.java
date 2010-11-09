package org.infinitest;

import static org.infinitest.InfinitestSettings.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class WhenConfiguringInfinitest
{
    @Test
    public void shouldProvideDefaultValues()
    {
        InfinitestSettings settings = new InfinitestSettings();
        assertTrue(settings.isInfinitestEnabled());
    }

    @Test
    public void canLoadSettingsFromAStream() throws IOException
    {
        Properties properties = new Properties();
        properties.setProperty(IS_ENABLED, "true");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        properties.store(out, "");
        InfinitestSettings settings = new InfinitestSettings(new ByteArrayInputStream(out.toByteArray()));
        assertTrue(settings.isInfinitestEnabled());
    }

    @Test
    public void canUpdateSettingsInPlace()
    {
        InfinitestSettings settings = new InfinitestSettings();
        settings.setIsInfinitestEnabled(false);
        assertFalse(settings.isInfinitestEnabled());
    }

    @Test
    public void canSavePropertiesToAStream() throws IOException
    {
        InfinitestSettings settings = new InfinitestSettings();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        settings.saveTo(outputStream);
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals("true", properties.getProperty(IS_ENABLED));
    }
}
