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
import static org.infinitest.config.FileBasedInfinitestConfigurationSource.INFINITEST_FILTERS_FILE_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.infinitest.intellij.IntellijMockBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;

class IdeaModuleSettingsTest extends IntellijMockBase {

	@Test
	void listOutputDirectories() {
		setupApplication();
		
		ModuleRootManager rootManager = mock(ModuleRootManager.class);
		CompilerModuleExtension compilerModuleExtension = mock(CompilerModuleExtension.class);
		
		when(module.getComponent(ModuleRootManager.class)).thenReturn(rootManager);
		when(rootManager.getDependencies()).thenReturn(new Module[0]);
		when(rootManager.getModuleExtension(CompilerModuleExtension.class)).thenReturn(compilerModuleExtension);
		when(compilerModuleExtension.getCompilerOutputUrl()).thenReturn("/project/target/classes");
		when(compilerModuleExtension.getCompilerOutputUrlForTests()).thenReturn("/project/target/test-classes");
		IdeaModuleSettings settings = new IdeaModuleSettings(module);
		
		List<File> outputDirs = settings.listOutputDirectories();
		
		assertThat(outputDirs).hasSize(2);
	}

	@Test
	void getRuntimeEnvironment() {
		IdeaModuleSettings settings = new IdeaModuleSettings(module) {
			@Override
			protected String infinitestJarPath(String jarName) {
				return "infinitest.jar";
			}
			
			@Override
			protected File getWorkingDirectory() {
				return new File(".");
			}
		};
		
		assertNotNull(settings.getRuntimeEnvironment());
	}
	
	@Test
	void getAbsentFilterFileFromWorkingDirectory(@TempDir File moduleRoot, @TempDir File projectRoot) {
		IdeaModuleSettings settings = new IdeaModuleSettings(module) {
			@Override
			protected File getWorkingDirectory() {
				return moduleRoot;
			}
		};
		
		when(project.getBasePath()).thenReturn(projectRoot.getAbsolutePath());
		
		assertNull(settings.getFilterFile());
	}
	

	
	@Test
	void getFilterFileFromWorkingDirectory(@TempDir File moduleRoot, @TempDir File projectRoot) throws IOException {
		IdeaModuleSettings settings = new IdeaModuleSettings(module) {
			@Override
			protected File getWorkingDirectory() {
				return moduleRoot;
			}
		};
		
		File filterFile = new File(moduleRoot, INFINITEST_FILTERS_FILE_NAME);
		filterFile.createNewFile();
		
		when(project.getBasePath()).thenReturn(projectRoot.getAbsolutePath());
		
		assertEquals(filterFile, settings.getFilterFile());
	}
	
	@Test
	void getFilterFileFromProjectDirectory(@TempDir File moduleRoot, @TempDir File projectRoot) throws IOException {
		IdeaModuleSettings settings = new IdeaModuleSettings(module) {
			@Override
			protected File getWorkingDirectory() {
				return moduleRoot;
			}
		};
		
		File filterFile = new File(projectRoot, INFINITEST_FILTERS_FILE_NAME);
		filterFile.createNewFile();
		
		when(project.getBasePath()).thenReturn(projectRoot.getAbsolutePath());
		
		assertEquals(filterFile, settings.getFilterFile());
	}
}
