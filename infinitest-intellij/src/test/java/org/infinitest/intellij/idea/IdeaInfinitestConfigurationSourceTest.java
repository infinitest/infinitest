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

import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.intellij.ModuleSettings;
import org.junit.jupiter.api.Test;

/**
 * @author gtoison
 *
 */
class IdeaInfinitestConfigurationSourceTest extends IntellijMockBase {

	@Test
	void noFilterFile() {
		ModuleSettings moduleSettings = mock(ModuleSettings.class);
		when(module.getService(ModuleSettings.class)).thenReturn(moduleSettings);
		
		IdeaInfinitestConfigurationSource configurationSource = new IdeaInfinitestConfigurationSource(module);
		
		assertNotNull(configurationSource.getConfiguration());
		
		verify(moduleSettings, times(1)).getFilterFile();
	}
	
	@Test
	void filterFile() {
		ModuleSettings moduleSettings = mock(ModuleSettings.class);
		when(module.getService(ModuleSettings.class)).thenReturn(moduleSettings);
		when(moduleSettings.getFilterFile()).thenReturn(new File(INFINITEST_FILTERS_FILE_NAME));
		
		IdeaInfinitestConfigurationSource configurationSource = new IdeaInfinitestConfigurationSource(module);
		
		assertNotNull(configurationSource.getConfiguration());
		
		verify(moduleSettings, times(1)).getFilterFile();
	}
}
