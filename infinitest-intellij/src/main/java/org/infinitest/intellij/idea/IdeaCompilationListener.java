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

import org.infinitest.*;
import org.infinitest.environment.RuntimeEnvironment;
import org.infinitest.intellij.*;

import com.intellij.openapi.compiler.*;

public class IdeaCompilationListener implements CompilationStatusListener, TestControl {
	private final InfinitestCore core;
	private final ModuleSettings moduleSettings;
	private boolean shouldRunTests = true;

	public IdeaCompilationListener(InfinitestCore core, ModuleSettings moduleSettings) {
		this.core = core;
		this.moduleSettings = moduleSettings;
	}

	@Override
	public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
		if (!aborted && (errors == 0)) {
			doRunTests();
		}
	}

	@Override
	public void fileGenerated(String outputRoot, String relativePath) {
		doRunTests();
	}

	@Override
	public void setRunTests(boolean shouldRunTests) {
		if (shouldRunTests && !this.shouldRunTests) {
			core.reload();
		}
		this.shouldRunTests = shouldRunTests;
	}

	@Override
	public boolean shouldRunTests() {
		return shouldRunTests;
	}

	private void doRunTests() {
		if (!shouldRunTests) {
			return;
		}

		RuntimeEnvironment runtimeEnvironment = moduleSettings.getRuntimeEnvironment();
		if (runtimeEnvironment == null) {
			return;
		}

		core.setRuntimeEnvironment(runtimeEnvironment);
		core.update();
	}
}
