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

import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;
import static org.eclipse.jface.preference.FieldEditor.*;
import static org.infinitest.eclipse.prefs.PreferencesConstants.*;
import static org.infinitest.util.InfinitestGlobalSettings.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.*;
import org.eclipse.swt.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.markers.*;
import org.infinitest.eclipse.trim.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenPreferencesAreChanged {
	private FieldEditor eventSource;
	private PluginActivationController controller;
	private PreferenceChangeHandler handler;
	private CoreSettings coreSettings;
	
	private SlowMarkerRegistry slowMarkerRegistry;
	private ProblemMarkerRegistry problemMarkerRegistry;

	@BeforeEach
	void setUp() {
		controller = mock(PluginActivationController.class);
		coreSettings = mock(CoreSettings.class);
		eventSource = mock(BooleanFieldEditor.class);
		
		slowMarkerRegistry = mock(SlowMarkerRegistry.class);
		problemMarkerRegistry = mock(ProblemMarkerRegistry.class);

		handler = new PreferenceChangeHandler(controller, coreSettings);
		handler.setSlowMarkerRegistry(slowMarkerRegistry);
		handler.setProblemMarkerRegistry(problemMarkerRegistry);
	}

	@AfterEach
	void cleanup() {
		resetToDefaults();
	}

	@Test
	void shouldStartContinouslyTestingIfSelectionIsChecked() {
		when(eventSource.getPreferenceName()).thenReturn(AUTO_TEST);
		changeProperty(VALUE, false, true);
		verify(controller).enable();
	}

	@Test
	void shouldStopContinouslyTestingIfSelectionIsChecked() {
		when(eventSource.getPreferenceName()).thenReturn(AUTO_TEST);
		changeProperty(VALUE, true, false);
		verify(controller).disable();
	}

	@Test
	void shouldIgnoreIfPropertyNameDoesNotMatch() {
		when(eventSource.getPreferenceName()).thenReturn("SomeEventName");
		changeProperty(VALUE, true, false);
		verifyNoInteractions(controller);
	}

	@Test
	void shouldAdjustWarningTimeout() {
		when(eventSource.getPreferenceName()).thenReturn(SLOW_TEST_WARNING);
		changeProperty(VALUE, "500", "100");
		assertEquals(100, getSlowTestTimeLimit());
	}

	@Test
	void shouldAdjustFailingBackgroundColor() {
		when(eventSource.getPreferenceName()).thenReturn(PreferencesConstants.FAILING_BACKGROUND_COLOR);
		int red = SWT.COLOR_DARK_RED;
		int blue = SWT.COLOR_BLUE;

		changeProperty(SwtColorFieldEditor.VALUE, String.valueOf(red), String.valueOf(blue));

		assertEquals(blue, ColorSettings.getFailingBackgroundColor());
	}

	@Test
	void shouldAdjustFailingTextColor() {
		when(eventSource.getPreferenceName()).thenReturn(PreferencesConstants.FAILING_TEXT_COLOR);
		int white = SWT.COLOR_WHITE;
		int yellow = SWT.COLOR_YELLOW;

		changeProperty(SwtColorFieldEditor.VALUE, String.valueOf(white), String.valueOf(yellow));

		assertEquals(yellow, ColorSettings.getFailingTextColor());
	}

	@Test
	void shouldMarketsWhenFailedTestSeverityChanges() {
		when(eventSource.getPreferenceName()).thenReturn(FAILED_TEST_MARKER_SEVERITY);
		changeProperty(SwtColorFieldEditor.VALUE, "2", "1");
		verify(problemMarkerRegistry).updateMarkersSeverity(SEVERITY_WARNING);
	}

	@Test
	void shouldMarketsWhenSlowTestSeverityChanges() {
		when(eventSource.getPreferenceName()).thenReturn(SLOW_TEST_MARKER_SEVERITY);
		changeProperty(SwtColorFieldEditor.VALUE, "2", "1");
		verify(slowMarkerRegistry).updateMarkersSeverity(SEVERITY_WARNING);
	}

	@Test
	void shouldAdjustSemaphorePermits() {
		when(eventSource.getPreferenceName()).thenReturn(PARALLEL_CORES);
		changeProperty(VALUE, "1", "2");
		verify(coreSettings).setConcurrentCoreCount(2);
	}

	@Test
	void shouldIgnoreOtherPropertyTypes() {
		when(eventSource.getPreferenceName()).thenReturn(PARALLEL_CORES);
		changeProperty(IS_VALID, false, true);
		verifyNoInteractions(controller);
	}

	@Test
	void shouldIgnoreBlankValues() {
		when(eventSource.getPreferenceName()).thenReturn(PARALLEL_CORES);
		assertDoesNotThrow(() -> changeProperty(VALUE, "", ""));
	}

	private void changeProperty(String type, Object oldValue, Object newValue) {
		handler.propertyChange(new PropertyChangeEvent(eventSource, type, oldValue, newValue));
	}
}
