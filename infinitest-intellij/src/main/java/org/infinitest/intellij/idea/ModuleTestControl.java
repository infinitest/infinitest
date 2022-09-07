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
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;

import com.intellij.openapi.module.Module;

public class ModuleTestControl implements TestControl {
	private static final long serialVersionUID = 7055308263165987253L;
	
	private Module module;
	private boolean shouldRunTests = true;
	
	/**
	 * @param module Injected by the platform
	 */
	public ModuleTestControl(Module module) {
		this.module = module;
	}

	@Override
	public void setRunTests(boolean shouldRunTests) {
		if (shouldRunTests && !shouldRunTests()) {
			TestControl projectTestControl = module.getProject().getService(TestControl.class);
			
			if (projectTestControl.shouldRunTests()) {
				InfinitestLauncher launcher = module.getService(InfinitestLauncher.class);
				InfinitestCore core = launcher.getCore();
				core.update();
			}
		}
		this.shouldRunTests = shouldRunTests;
	}

	@Override
	public boolean shouldRunTests() {
		return shouldRunTests;
	}
}
