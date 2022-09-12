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

import org.infinitest.InfinitestCore;
import org.infinitest.TestControl;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.intellij.ModuleSettings;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.task.ProjectTaskListener;
import com.intellij.task.ProjectTaskManager.Result;

public class IdeaCompilationListener implements ProjectTaskListener {
	private final Project project;

	/**
	 * @param project Injected by the platform
	 */
	public IdeaCompilationListener(Project project) {
		this.project = project;
	}
	
	@Override
	public void finished(@NotNull Result result) {
		if (!result.isAborted() && !result.hasErrors()) {
			doRunTests();
		}
	}

	private void doRunTests() {
		TestControl testControl = project.getService(ProjectTestControl.class);
		
		if (testControl.shouldRunTests()) {
			for (Module module : ModuleManager.getInstance(project).getModules()) {
				if (testControl.shouldRunTests(module)) {
					InfinitestLauncher launcher = module.getService(InfinitestLauncher.class);
					ModuleSettings moduleSettings = module.getService(ModuleSettings.class);

					RuntimeEnvironment runtimeEnvironment = moduleSettings.getRuntimeEnvironment();
					if (runtimeEnvironment != null) {
						InfinitestCore core = launcher.getCore();

						core.setRuntimeEnvironment(runtimeEnvironment);
						core.update();
					}
				}
			}
		}
	}
}
