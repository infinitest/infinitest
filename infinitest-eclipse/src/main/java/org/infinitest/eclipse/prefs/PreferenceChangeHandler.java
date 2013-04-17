/*
 * Infinitest, a Continuous Test Runner.
 *
 * Copyright (C) 2010-2013
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>
 * "David Gageot" <david@gageot.net>, et al.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.infinitest.eclipse.prefs;

import static java.lang.Integer.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.eclipse.jface.preference.FieldEditor.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class PreferenceChangeHandler {
	private final PluginActivationController controller;
	private final CoreSettings coreSettings;
	private SlowMarkerRegistry slowMarkerRegistry;
	private boolean clearSlowMarkerRegistry;

	@Autowired
	public PreferenceChangeHandler(PluginActivationController controller, CoreSettings coreSettings) {
		this.controller = controller;
		this.coreSettings = coreSettings;
	}

	public void propertyChange(PropertyChangeEvent event) {
		String preference = findChangedPreference(event);
		Object newValue = event.getNewValue();
		if (AUTO_TEST.equals(preference)) {
			updateAutoTest((Boolean) newValue);
		}

		if (SLOW_TEST_WARNING.equals(preference)) {
			updateSlowTestWarning((String) newValue);
		}

		if (PARALLEL_CORES.equals(preference)) {
			updateConcurrency((String) newValue);
		}
	}

	private void updateConcurrency(String newValue) {
		if (!isBlank(newValue)) {
			coreSettings.setConcurrentCoreCount(Integer.parseInt(newValue));
		}
	}

	private void updateSlowTestWarning(String newValue) {
		if (!isBlank(newValue)) {
			setSlowTestTimeLimit(parseInt(newValue));
			// Remove markers created per previous value
			clearSlowMarkerRegistry = true;
		}

	}

	private void updateAutoTest(Boolean continuouslyTest) {
		if (continuouslyTest.booleanValue()) {
			controller.enable();
		} else {
			controller.disable();
		}
	}

	private String findChangedPreference(PropertyChangeEvent event) {
		Object source = event.getSource();
		if ((source instanceof FieldEditor) && event.getProperty().equals(VALUE)) {
			return ((FieldEditor) source).getPreferenceName();
		}
		return null;
	}

	public void setSlowMarkerRegistry(SlowMarkerRegistry bean) {
		slowMarkerRegistry = bean;
		clearSlowMarkerRegistry = false;
	}

	public void applyChanges() {
		if (clearSlowMarkerRegistry) {
			clearSlowMarkers();
		}

	}

	public void clearChanges() {
		clearSlowMarkerRegistry = false;
	}

	public void clearSlowMarkers() {
		clearSlowMarkerRegistry = false;
		if (slowMarkerRegistry != null) {
			slowMarkerRegistry.clear();
		}
	}
}
