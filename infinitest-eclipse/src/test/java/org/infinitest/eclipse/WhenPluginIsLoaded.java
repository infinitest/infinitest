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
package org.infinitest.eclipse;

import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.*;
import org.infinitest.eclipse.workspace.*;
import org.infinitest.util.*;
import org.junit.*;

public class WhenPluginIsLoaded {
	private InfinitestPlugin plugin;
	private Preferences preferences;
	private CoreSettings coreSettings;

	@Before
	public void inContext() {
		preferences = mock(Preferences.class);
		coreSettings = mock(CoreSettings.class);

		plugin = new InfinitestPlugin();
	}

	@Test
	public void shouldRestoreSavedPreferences() {
		when(preferences.getInt(PARALLEL_CORES)).thenReturn(4);
		when(preferences.getLong(SLOW_TEST_WARNING)).thenReturn(1000L);

		plugin.restoreSavedPreferences(preferences, coreSettings);

		verify(coreSettings).setConcurrentCoreCount(4);
		assertEquals(1000L, InfinitestGlobalSettings.getSlowTestTimeLimit());
	}
}
