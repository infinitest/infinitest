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

import static java.lang.Integer.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.eclipse.jface.preference.FieldEditor.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.infinitest.eclipse.PluginActivationController;
import org.infinitest.eclipse.workspace.CoreSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PreferenceChangeHandler
{
    private final PluginActivationController controller;
    private final CoreSettings coreSettings;

    @Autowired
    public PreferenceChangeHandler(PluginActivationController controller, CoreSettings coreSettings)
    {
        this.controller = controller;
        this.coreSettings = coreSettings;
    }

    public void propertyChange(PropertyChangeEvent event)
    {
        String preference = findChangedPreference(event);
        if (AUTO_TEST.equals(preference))
        {
            updateAutoTest(event);
        }

        if (SLOW_TEST_WARNING.equals(preference))
        {
            updateSlowTestWarning(event);
        }

        if (PARALLEL_CORES.equals(preference))
        {
            updateConcurrency(event);
        }
    }

    private void updateConcurrency(PropertyChangeEvent event)
    {
        String newValue = event.getNewValue().toString();
        if (!isBlank(newValue))
        {
            coreSettings.setConcurrentCoreCount(Integer.parseInt(newValue));
        }
    }

    private void updateSlowTestWarning(PropertyChangeEvent event)
    {
        // DEBT Duplication?
        String newValue = event.getNewValue().toString();
        if (!isBlank(newValue))
        {
            setSlowTestTimeLimit(parseInt(newValue));
            // Could remove markers that are no longer valid if this value is raised
        }
    }

    private void updateAutoTest(PropertyChangeEvent event)
    {
        Boolean continuouslyTest = (Boolean) event.getNewValue();
        if (continuouslyTest.booleanValue())
        {
            controller.enable();
        }
        else
        {
            controller.disable();
        }
    }

    private String findChangedPreference(PropertyChangeEvent event)
    {
        Object source = event.getSource();
        if (source instanceof FieldEditor && event.getProperty().equals(VALUE))
        {
            return ((FieldEditor) source).getPreferenceName();
        }
        return null;
    }
}
