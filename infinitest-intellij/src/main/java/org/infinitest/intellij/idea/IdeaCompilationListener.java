/*
 * This file is part of Infinitest.
 *
 * Copyright (C) 2010
 * "Ben Rady" <benrady@gmail.com>,
 * "Rod Coffin" <rfciii@gmail.com>,
 * "Ryan Breidenbach" <ryan.breidenbach@gmail.com>, et al.
 *
 * Infinitest is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitest is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Infinitest.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.infinitest.intellij.idea;

import org.infinitest.InfinitestCore;
import org.infinitest.RuntimeEnvironment;
import org.infinitest.TestControl;
import org.infinitest.intellij.ModuleSettings;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;

public class IdeaCompilationListener implements CompilationStatusListener, TestControl {

	private final InfinitestCore core;
	private final ModuleSettings moduleSettings;
	private boolean shouldRunTests = true;

	public IdeaCompilationListener(InfinitestCore core, ModuleSettings moduleSettings) {
		this.core = core;
		this.moduleSettings = moduleSettings;
	}

	public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
		RuntimeEnvironment runtimeEnvironment = moduleSettings.getRuntimeEnvironment();
		if (runtimeEnvironment == null) {
			return;
		}

		if (!aborted && (errors == 0)) {
			core.setRuntimeEnvironment(runtimeEnvironment);
			if (shouldRunTests) {
				core.update();
			}
		}
	}

	public void fileGenerated(String outputRoot, String relativePath) {
		core.update();
	}

	public void setRunTests(boolean shouldRunTests) {
		if (shouldRunTests && !this.shouldRunTests) {
			core.reload();
		}
		this.shouldRunTests = shouldRunTests;
	}

	public boolean shouldRunTests() {
		return shouldRunTests;
	}
}
