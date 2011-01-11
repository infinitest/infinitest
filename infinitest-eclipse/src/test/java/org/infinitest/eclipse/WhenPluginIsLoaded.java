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
package org.infinitest.eclipse;

import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.Preferences;
import org.infinitest.eclipse.workspace.CoreSettings;
import org.infinitest.util.InfinitestGlobalSettings;
import org.junit.Before;
import org.junit.Test;

public class WhenPluginIsLoaded
{
    private InfinitestPlugin plugin;
    private Preferences preferences;
    private CoreSettings coreSettings;

    @Before
    public void inContext()
    {
        preferences = mock(Preferences.class);
        coreSettings = mock(CoreSettings.class);

        plugin = new InfinitestPlugin();
    }

    @Test
    public void shouldRestoreSavedPreferences()
    {
        when(preferences.getInt(PARALLEL_CORES)).thenReturn(4);
        when(preferences.getLong(SLOW_TEST_WARNING)).thenReturn(1000L);

        plugin.restoreSavedPreferences(preferences, coreSettings);

        verify(coreSettings).setConcurrentCoreCount(4);
        assertEquals(1000L, InfinitestGlobalSettings.getSlowTestTimeLimit());
    }
}
