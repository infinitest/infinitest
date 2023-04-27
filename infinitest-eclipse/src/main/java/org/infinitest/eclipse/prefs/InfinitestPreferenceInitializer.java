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

import static org.infinitest.eclipse.prefs.PreferencesConstants.DISABLE_WHEN_WORKSPACE_HAS_ERRORS;
import static org.infinitest.eclipse.prefs.PreferencesConstants.FAILING_BACKGROUND_COLOR;
import static org.infinitest.eclipse.prefs.PreferencesConstants.FAILING_TEXT_COLOR;
import static org.infinitest.eclipse.prefs.PreferencesConstants.PARALLEL_CORES;
import static org.infinitest.eclipse.prefs.PreferencesConstants.SLOW_TEST_WARNING;
import static org.infinitest.util.InfinitestGlobalSettings.DEFAULT_SLOW_TEST_LIMIT;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.infinitest.eclipse.InfinitestPlugin;

public class InfinitestPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(InfinitestPlugin.PLUGIN_ID);
		
		initializePreferenceNodeWithDefaults(node);
	}

	public void initializePreferenceNodeWithDefaults(IEclipsePreferences node) {
		node.put(PARALLEL_CORES, Integer.toString(1));
		node.put(DISABLE_WHEN_WORKSPACE_HAS_ERRORS, Boolean.toString(false));
		node.put(SLOW_TEST_WARNING, Long.toString(DEFAULT_SLOW_TEST_LIMIT));
		node.put(FAILING_BACKGROUND_COLOR, Integer.toString(SWT.COLOR_DARK_RED));
		node.put(FAILING_TEXT_COLOR, Integer.toString(SWT.COLOR_WHITE));
	}
}
