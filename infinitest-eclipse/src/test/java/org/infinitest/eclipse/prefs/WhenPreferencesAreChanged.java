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

import static org.eclipse.jface.preference.FieldEditor.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.*;

public class WhenPreferencesAreChanged {
	private FieldEditor eventSource;
	private PluginActivationController controller;
	private PreferenceChangeHandler handler;
	private CoreSettings coreSettings;

	@Before
	public void setUp() {
		controller = mock(PluginActivationController.class);
		coreSettings = mock(CoreSettings.class);
		eventSource = mock(BooleanFieldEditor.class);

		handler = new PreferenceChangeHandler(controller, coreSettings);
		handler.setSlowMarkerRegistry(new SlowMarkerRegistry());
	}

	@After
	public void cleanup() {
		resetToDefaults();
	}

	@Test
	public void shouldStartContinouslyTestingIfSelectionIsChecked() {
		when(eventSource.getPreferenceName()).thenReturn(AUTO_TEST);
		changeProperty(VALUE, false, true);
		verify(controller).enable();
	}

	@Test
	public void shouldStopContinouslyTestingIfSelectionIsChecked() {
		when(eventSource.getPreferenceName()).thenReturn(AUTO_TEST);
		changeProperty(VALUE, true, false);
		verify(controller).disable();
	}

	@Test
	public void shouldIgnoreIfPropertyNameDoesNotMatch() {
		when(eventSource.getPreferenceName()).thenReturn("SomeEventName");
		changeProperty(VALUE, true, false);
		verifyZeroInteractions(controller);
	}

	@Test
	public void shouldAdjustWarningTimeout() {
		when(eventSource.getPreferenceName()).thenReturn(SLOW_TEST_WARNING);
		changeProperty(VALUE, "500", "100");
		assertEquals(100, getSlowTestTimeLimit());
	}

	@Test
	public void shouldAdjustSemaphorePermits() {
		when(eventSource.getPreferenceName()).thenReturn(PARALLEL_CORES);
		changeProperty(VALUE, "1", "2");
		verify(coreSettings).setConcurrentCoreCount(2);
	}

	@Test
	public void shouldIgnoreOtherPropertyTypes() {
		when(eventSource.getPreferenceName()).thenReturn(PARALLEL_CORES);
		changeProperty(IS_VALID, false, true);
		verifyZeroInteractions(controller);
	}

	@Test
	public void shouldIgnoreBlankValues() {
		when(eventSource.getPreferenceName()).thenReturn(PARALLEL_CORES);
		changeProperty(VALUE, "", "");
	}

	private void changeProperty(String type, Object oldValue, Object newValue) {
		handler.propertyChange(new PropertyChangeEvent(eventSource, type, oldValue, newValue));
	}
}
