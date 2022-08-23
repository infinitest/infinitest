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
import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import org.junit.*;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;

public class WhenBuildingIdeaModuleClassPath {
	private static final String PATH_A = "a";
	private static final String PATH_B = "b";
	private static final String PATH_C = "c";
	private static final String PATH_D = "d";

	private final CompilerModuleExtension compilerModuleExtension = mock(CompilerModuleExtension.class);
	private final ModuleRootManager moduleRootManagerMock = mock(ModuleRootManager.class);
	private final Module module = mock(Module.class);
	private final IdeaModuleSettings ideaModuleSettingsSpy = spy(new IdeaModuleSettings(module));

	@Test
	public void shouldIncludeAllCompilationClassesToClasspathElementsList() {
		OrderEntry orderEntry1 = orderEntry();
		OrderEntry orderEntry2 = orderEntry();
		doReturn(new OrderEntry[] { orderEntry1, orderEntry2 }).when(moduleRootManagerMock).getOrderEntries();
		doReturn(new VirtualFile[] { fileWith(PATH_D) }).when(compilerModuleExtension).getOutputRoots(true);
		doReturn(new VirtualFile[] { fileWith(PATH_A), fileWith(PATH_B) }).when(orderEntry1).getFiles(OrderRootType.CLASSES);
		doReturn(new VirtualFile[] { fileWith(PATH_C) }).when(orderEntry2).getFiles(OrderRootType.CLASSES);
		doReturn(moduleRootManagerMock).when(ideaModuleSettingsSpy).moduleRootManagerInstance();
		doReturn(compilerModuleExtension).when(ideaModuleSettingsSpy).compilerModuleExtension();

		final List<File> classPathElementsList = ideaModuleSettingsSpy.listClasspathElements();

		assertThat(classPathElementsList.get(0).getPath(), equalTo(PATH_A));
		assertThat(classPathElementsList.get(1).getPath(), equalTo(PATH_B));
		assertThat(classPathElementsList.get(2).getPath(), equalTo(PATH_C));
		assertThat(classPathElementsList.get(3).getPath(), equalTo(PATH_D));
		assertThat(classPathElementsList.size(), equalTo(4));
	}
	
	@Test
	public void shouldExcludeJdkModules() {
		OrderEntry jdkOrderEntry = mock(JdkOrderEntry.class);
		doReturn(new OrderEntry[] { jdkOrderEntry }).when(moduleRootManagerMock).getOrderEntries();
		doReturn(new VirtualFile[] { fileWith(PATH_D) }).when(compilerModuleExtension).getOutputRoots(true);
		doReturn(new VirtualFile[] { fileWith(PATH_C) }).when(jdkOrderEntry).getFiles(OrderRootType.CLASSES);
		doReturn(moduleRootManagerMock).when(ideaModuleSettingsSpy).moduleRootManagerInstance();
		doReturn(compilerModuleExtension).when(ideaModuleSettingsSpy).compilerModuleExtension();
		
		final List<File> classPathElementsList = ideaModuleSettingsSpy.listClasspathElements();
		
		verifyNoInteractions(jdkOrderEntry);
		assertThat(classPathElementsList).hasSize(1);
	}

	private static OrderEntry orderEntry() {
		return mock(OrderEntry.class);
	}

	private static VirtualFile fileWith(final String path) {
		VirtualFile virtualFile = mock(VirtualFile.class);
		doReturn(path).when(virtualFile).getPath();
		return virtualFile;
	}
}
