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
package org.infinitest.intellij.idea.facet;

import org.infinitest.intellij.idea.*;
import org.infinitest.intellij.idea.window.*;
import org.infinitest.intellij.plugin.*;
import org.infinitest.intellij.plugin.launcher.*;
import org.jdom.*;

import com.intellij.facet.*;
import com.intellij.facet.ui.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.*;
import com.intellij.openapi.wm.*;

public class InfinitestFacetConfiguration implements FacetConfiguration, InfinitestConfiguration {
	private Module module;
	private InfinitestConfigurationListener listener;

	public InfinitestFacetConfiguration() {
	}

	public void setModule(Module module) {
		this.module = module;
	}

	@Override
	public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
		return new FacetEditorTab[]{new InfinitestFacetEditorTab(this)};
	}

	@Override
	public void readExternal(Element config) {
	}

	@Override
	public void writeExternal(Element config) {
	}

	@Override
	public InfinitestLauncher createLauncher() {
		Project project = module.getProject();

		IdeaModuleSettings moduleSettings = new IdeaModuleSettings(module);
		IdeaToolWindowRegistry toolWindowRegistry = new IdeaToolWindowRegistry(project);
		IdeaCompilationNotifier compilationNotifier = new IdeaCompilationNotifier(project);
		IdeaSourceNavigator navigator = new IdeaSourceNavigator(project);
		FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
		ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

		return new InfinitestLauncherImpl(moduleSettings, toolWindowRegistry, compilationNotifier, navigator, fileEditorManager, toolWindowManager);
	}

	@Override
	public void registerListener(InfinitestConfigurationListener listener) {
		this.listener = listener;
	}

	public void updated() {
		if (listener != null) {
			listener.configurationUpdated(this);
		}
	}
}
