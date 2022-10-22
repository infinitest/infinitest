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
package org.infinitest.intellij.idea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.infinitest.InfinitestCore;
import org.infinitest.intellij.IntellijMockBase;
import org.junit.jupiter.api.Test;

class IdeaTestControlTest extends IntellijMockBase {
	@Test
	void projectControlTest() {
		setupApplication();
		
		ProjectTestControl testControl = new ProjectTestControl(project);
		
		when(module.getProject().getService(ProjectTestControl.class)).thenReturn(testControl);
		
		InfinitestCore core = mock(InfinitestCore.class);
		
		when(launcher.getCore()).thenReturn(core);
		
		testControl.setRunTests(false);
		
		verify(core, never()).reload();
		
		testControl.setRunTests(true);
		
		verify(core).update();
	}
	
	@Test
	void updateModuleState() {
		ProjectTestControl testControl = new ProjectTestControl(project);
		ProjectTestControlState state = new ProjectTestControlState();
		
		InfinitestCore core = mock(InfinitestCore.class);
		
		when(launcher.getCore()).thenReturn(core);
		
		// Load an empty state and disable tests on a module
		testControl.loadState(state);
		testControl.setRunTests(false, module);
		
		assertFalse(testControl.shouldRunTests(module));
		assertThat(testControl.getState().getDisabledModulesNames()).contains(module.getName());

		// Now enable the tests on the module
		testControl.setRunTests(true, module);
		
		verify(core).update();
		assertThat(testControl.getState().getDisabledModulesNames()).isEmpty();
	}
	
	@Test
	void enablePowerSaveMode() {
		setupApplication(true);
		
		ProjectTestControl testControl = new ProjectTestControl(project);
		
		when(module.getProject().getService(ProjectTestControl.class)).thenReturn(testControl);
		
		InfinitestCore core = mock(InfinitestCore.class);
		
		when(launcher.getCore()).thenReturn(core);
		
		testControl.setRunTests(true);
		
		verify(core, never()).reload();
	}
}
