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

import java.util.List;

import org.infinitest.InfinitestCore;
import org.infinitest.TestControl;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

/**
 * Watches for changes (saved, deleted, created, etc.) on files named 'infinitest.filters'. When a change is detected
 * in a project this triggers a run of all the enabled tests. We're running all the tests because the filters mix
 * different kinds of logic to exclude/include tests (regex on the name, JUnit annotations) so the plugin cannot
 * easily compute a diff of the new/old tests
 * 
 * @author gtoison
 */
public class FilterFileWatcher implements BulkFileListener {
	private Project project;

	public FilterFileWatcher(Project project) {
		this.project = project;
	}
	
	@Override
	public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
		ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(project);
		
		for (VFileEvent event : events) {
			if (event.getPath().endsWith(INFINITEST_FILTERS_FILE_NAME)) {
				VirtualFile file = event.getFile();
				// The javadoc says that the file might be null
				if (file != null && projectFileIndex.isInContent(file)) {
					runEnabledTests();
					return;
				}
			}
		}
	}

	public void runEnabledTests() {
		TestControl testControl = project.getService(ProjectTestControl.class);
		if (testControl.shouldRunTests()) {
			for (Module module : ModuleManager.getInstance(project).getModules()) {
				if (testControl.shouldRunTests(module)) {
					InfinitestLauncher launcher = module.getService(InfinitestLauncher.class);
					InfinitestCore core = launcher.getCore();
					
					core.filterFileWasUpdated();
				}
			}
		}
	}
}
