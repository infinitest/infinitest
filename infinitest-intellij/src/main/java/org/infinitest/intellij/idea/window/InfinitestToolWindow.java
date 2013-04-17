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
package org.infinitest.intellij.idea.window;

import org.infinitest.intellij.idea.facet.*;
import org.infinitest.intellij.plugin.*;
import org.jetbrains.annotations.*;

import com.intellij.facet.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.startup.*;

public class InfinitestToolWindow implements ModuleComponent, FacetListener {
	public static final String TOOL_WINDOW_ID = "Infinitest";

	private InfinitestPlugin plugin;
	private final Project project;
	private final Module module;
	private boolean projectOpened;

	public InfinitestToolWindow(Project project, Module module) {
		this.project = project;
		this.module = module;
	}

	@Override
	public void initComponent() {
		// nothing to do here
	}

	@Override
	public void disposeComponent() {
		// nothing to do here
	}

	@Override
	@NotNull
	public String getComponentName() {
		return "org.infinitest.intellij.idea.window.InfinitestToolWindow";
	}

	public void startInfinitestAfterStartup() {
		StartupManager.getInstance(project).registerPostStartupActivity(new Runnable() {
			@Override
			public void run() {
				plugin.startInfinitest();
			}
		});
	}

	@Override
	public void facetInitialized() {
		if (plugin != null) {
			plugin.stopInfinitest();
		}

		InfinitestFacet facet = FacetManager.getInstance(module).getFacetByType(InfinitestFacet.ID);
		plugin = new InfinitestPluginImpl(facet.getConfiguration());

		if (projectOpened) {
			plugin.startInfinitest();
		} else {
			startInfinitestAfterStartup();
		}
	}

	@Override
	public void facetDisposed() {
		if (plugin != null) {
			plugin.stopInfinitest();
		}
	}

	@Override
	public void projectOpened() {
		projectOpened = true;
	}

	@Override
	public void projectClosed() {
		if (plugin != null) {
			plugin.stopInfinitest();
		}
	}

	@Override
	public void moduleAdded() {
		// nothing to do here
	}
}
