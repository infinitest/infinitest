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
package org.infinitest.eclipse.prefs;

import static org.easymock.EasyMock.*;
import static org.eclipse.jface.preference.FieldEditor.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.junit.Assert.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.infinitest.eclipse.PluginActivationController;
import org.infinitest.eclipse.workspace.CoreSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class WhenPreferencesAreChanged
{
    private FieldEditor eventSource;
    private String preferenceName;
    private PluginActivationController controller;
    private PreferenceChangeHandler handler;
    private CoreSettings coreSettings;

    @Before
    public void setUp()
    {
        controller = createMock(PluginActivationController.class);
        coreSettings = Mockito.mock(CoreSettings.class);
        handler = new PreferenceChangeHandler(controller, coreSettings);
        preferenceName = AUTO_TEST;
        eventSource = new BooleanFieldEditor()
        {
            @Override
            public String getPreferenceName()
            {
                return preferenceName;
            }
        };
    }

    @After
    public void cleanup()
    {
        resetToDefaults();
    }

    @Test
    public void shouldStartContinouslyTestingIfSelectionIsChecked()
    {
        controller.enable();
        checkExpectations(AUTO_TEST, false, true);
    }

    @Test
    public void shouldStopContinouslyTestingIfSelectionIsChecked()
    {
        controller.disable();
        checkExpectations(AUTO_TEST, true, false);
    }

    @Test
    public void shouldIgnoreIfPropertyNameDoesNotMatch()
    {
        preferenceName = "SomeEventName";
        checkExpectations(preferenceName, true, false);
    }

    @Test
    public void shouldPromptForLicenseIfPluginIsEnabledWithInvalidKey()
    {
        controller.enable();
        // Show license dialog now
        checkExpectations(AUTO_TEST, false, true);
    }

    @Test
    public void shouldAdjustWarningTimeout()
    {
        preferenceName = SLOW_TEST_WARNING;
        checkExpectations(SLOW_TEST_WARNING, "500", "100");
        assertEquals(100, getSlowTestTimeLimit());
    }

    @Test
    public void shouldAdjustSemaphorePermits()
    {
        preferenceName = PARALLEL_CORES;
        checkExpectations(PARALLEL_CORES, "1", "2");
        Mockito.verify(coreSettings).setConcurrentCoreCount(2);
    }

    @Test
    public void shouldIgnoreOtherPropertyTypes()
    {
        replay(controller);
        preferenceName = PARALLEL_CORES;
        handler.propertyChange(new PropertyChangeEvent(eventSource, IS_VALID, false, true));
        verify(controller);
    }

    @Test
    public void shouldIgnoreBlankValues()
    {
        preferenceName = PARALLEL_CORES;
        checkExpectations(PARALLEL_CORES, "", "");
    }

    private void checkExpectations(String propertyName, Object previousState, Object newState)
    {
        replay(controller);
        handler.propertyChange(new PropertyChangeEvent(eventSource, VALUE, previousState, newState));
        verify(controller);
    }

}
