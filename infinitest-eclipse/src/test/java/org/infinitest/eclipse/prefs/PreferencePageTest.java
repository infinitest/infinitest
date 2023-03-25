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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.infinitest.eclipse.markers.ProblemMarkerRegistry;
import org.infinitest.eclipse.markers.SlowMarkerRegistry;
import org.junit.jupiter.api.Test;

class PreferencePageTest {

	@Test
	void test() throws Exception {
		PreferenceChangeHandler changeHandler = mock(PreferenceChangeHandler.class);
		ProblemMarkerRegistry problemMarkerRegistry = mock(ProblemMarkerRegistry.class);
		SlowMarkerRegistry slowMarkerRegistry = mock(SlowMarkerRegistry.class);
		
		Display display = Display.getCurrent();
		Composite parent = new Shell(display);
		
		PreferencePage preferencePage = new PreferencePage(changeHandler, problemMarkerRegistry, slowMarkerRegistry) {
			@Override
			protected Composite getFieldEditorParent() {
				return parent;
			}
		};
		
		preferencePage.createFieldEditors();
		preferencePage.performDefaults();
		
		verify(changeHandler).clearSlowMarkers();
	}
}
