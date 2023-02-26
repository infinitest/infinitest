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

import com.intellij.ide.PowerSaveMode;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.components.SettingsCategory;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

/**
 * A lightweight/project-level service to enable/disable tests globally or at module level
 */
@Service(Level.PROJECT)
@State(category = SettingsCategory.PLUGINS, name = "org.infinitest.intellij.idea.ProjectTestControl.shouldRunTests", storages =  {
		@Storage(value = StoragePathMacros.WORKSPACE_FILE, roamingType = RoamingType.DISABLED)
})
public final class ProjectTestControl implements TestControl, PersistentStateComponent<ProjectTestControlState> {
	private Project project;
	private ProjectTestControlState state = new ProjectTestControlState();
	
	/**
	 * @param project Injected by the platform
	 */
	public ProjectTestControl(Project project) {
		this.project = project;
	}

	@Override
	public void setRunTests(boolean shouldRunTests) {
		boolean previousStateRunTests = state.isRunTests();
		state.setRunTests(shouldRunTests);
		
		if (shouldRunTests && !previousStateRunTests) {
			for (Module module : ModuleManager.getInstance(project).getModules()) {
				if (shouldRunTests(module)) {
					runModuleTests(module);
				}
			}
		}
	}
	
	@Override
	public void setRunTests(boolean shouldRunTests, Module module) {
		if (state.isRunTests() && state.getDisabledModulesNames().contains(module.getName())) {
			runModuleTests(module);
		}
		
		if (shouldRunTests) {
			state.getDisabledModulesNames().remove(module.getName());
		} else {
			state.getDisabledModulesNames().add(module.getName());
		}
	}

	private void runModuleTests(Module module) {
		InfinitestLauncher launcher = module.getService(InfinitestLauncher.class);
		InfinitestCore core = launcher.getCore();
		core.update();
	}

	@Override
	public boolean shouldRunTests(Module module) {
		return !state.getDisabledModulesNames().contains(module.getName()) 
				&& !PowerSaveMode.isEnabled()
				&& moduleHasRuntimeEnvironment(module);
	}
	
	/**
	 * In case a module does not have a JDK (for instance a Javascript module) it won't have a JDK
	 * @param module The module
	 * @return <code>true</code> if the module has a {@link RuntimeEnvironment} we can use to run tests
	 */
	private boolean moduleHasRuntimeEnvironment(Module module) {
		ModuleSettings moduleSettings = module.getService(ModuleSettings.class);
		return moduleSettings.getRuntimeEnvironment() != null;
	}

	@Override
	public boolean shouldRunTests() {
		return state.isRunTests();
	}
	
	@Override
	public void loadState(ProjectTestControlState state) {
		this.state = state;
	}
	
	@Override
	public ProjectTestControlState getState() {
		return state;
	}
}
