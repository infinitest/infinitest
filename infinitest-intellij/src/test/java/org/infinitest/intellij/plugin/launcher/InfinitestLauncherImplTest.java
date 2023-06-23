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
package org.infinitest.intellij.plugin.launcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.infinitest.config.FileBasedInfinitestConfigurationSource;
import org.infinitest.config.InfinitestConfigurationSource;
import org.infinitest.intellij.IntellijMockBase;
import org.infinitest.intellij.ModuleSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intellij.openapi.util.Disposer;

class InfinitestLauncherImplTest extends IntellijMockBase {
	private ModuleSettings moduleSettings;
	
	@BeforeEach
	void setupInfinitestLauncherImplTest() {
		moduleSettings = mock(ModuleSettings.class);
		InfinitestConfigurationSource configurationSource = FileBasedInfinitestConfigurationSource.createFromCurrentWorkingDirectory();
		
		when(module.getService(ModuleSettings.class)).thenReturn(moduleSettings);
		when(moduleSettings.getRuntimeEnvironment()).thenReturn(runtimeEnvironment);
		when(runtimeEnvironment.getConfigurationSource()).thenReturn(configurationSource);		
	}

	@Test
	void launcherInitiatilization() {
		InfinitestLauncherImpl launcher = new InfinitestLauncherImpl(module);
		
		assertThat(launcher.getCore()).isNotNull();
		assertThat(launcher.getResultCollector()).isNotNull();
	}
	
	@Test
	void moduleDisposedShouldStopTests() {
		InfinitestLauncherImpl launcher = new InfinitestLauncherImpl(module);
		
		Disposer.dispose(module);
		
		assertThat(Disposer.getDisposalTrace(launcher)).isNotNull();
	}
}
