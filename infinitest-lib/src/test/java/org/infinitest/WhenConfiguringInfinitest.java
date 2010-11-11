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
