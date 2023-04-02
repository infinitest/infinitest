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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.infinitest.intellij.IntellijMockBase;
import org.junit.jupiter.api.Test;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootsEnumerator;
import com.intellij.openapi.vfs.VirtualFile;

class WhenBuildingIdeaModuleClassPath extends IntellijMockBase {
	private static final String PATH_A = "a";
	private static final String PATH_B = "b";
	private static final String PATH_C = "c";
	private static final String PATH_D = "d";

	@Test
	void shouldIncludeAllCompilationClassesToClasspathElementsList() {
		IdeaModuleSettings ideaModuleSettingsSpy = spy(new IdeaModuleSettings(module));
		
		OrderEnumerator orderEnumerator = mock(OrderEnumerator.class);
		OrderRootsEnumerator orderRootsEnumerator = mock(OrderRootsEnumerator.class);
		VirtualFile[] roots = new VirtualFile[] {
				fileWith(PATH_A),
				fileWith(PATH_B),
				fileWith(PATH_C),
				fileWith(PATH_D),
		};
		
		when(orderEnumerator.withoutSdk()).thenReturn(orderEnumerator);
		when(orderEnumerator.recursively()).thenReturn(orderEnumerator);
		when(orderEnumerator.classes()).thenReturn(orderRootsEnumerator);
		when(orderRootsEnumerator.getRoots()).thenReturn(roots);
		
		when(moduleRootManager.orderEntries()).thenReturn(orderEnumerator);
		
		final List<File> classPathElementsList = ideaModuleSettingsSpy.listClasspathElements();

		assertThat(classPathElementsList).hasSize(4);
		verify(orderEnumerator, times(1)).withoutSdk();
	}

	private static VirtualFile fileWith(final String path) {
		VirtualFile virtualFile = mock(VirtualFile.class);
		doReturn(path).when(virtualFile).getPath();
		return virtualFile;
	}
	
	@Test
	void moduleSdk() {
		IdeaModuleSettings settings = new IdeaModuleSettings(module);
		
		ModuleRootManager moduleRootManager = mock(ModuleRootManager.class);
		Sdk sdk = mock(Sdk.class);
		
		when(module.getComponent(ModuleRootManager.class)).thenReturn(moduleRootManager);
		when(moduleRootManager.getSdk()).thenReturn(sdk);
		when(sdk.getHomePath()).thenReturn("jdk");
		
		assertThat(settings.getSdkHomePath()).isEqualTo(new File("jdk"));
	}
}
