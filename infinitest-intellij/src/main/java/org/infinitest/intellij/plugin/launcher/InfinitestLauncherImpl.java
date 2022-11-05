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
package org.infinitest.intellij.plugin.launcher;

import org.infinitest.InfinitestCore;
import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.ResultCollector;
import org.infinitest.intellij.InfinitestTopics;
import org.infinitest.intellij.ModuleSettings;
import org.infinitest.intellij.plugin.swingui.SwingEventQueue;

import com.intellij.openapi.module.Module;

public class InfinitestLauncherImpl implements InfinitestLauncher {
	private final InfinitestCore core;
	private final ResultCollector resultCollector;
	
	/**
	 * @param module Injected by the platform
	 */
	public InfinitestLauncherImpl(Module module) {
		ModuleSettings moduleSettings = module.getService(ModuleSettings.class);
		InfinitestCoreBuilder coreBuilder = new InfinitestCoreBuilder(moduleSettings.getRuntimeEnvironment(), new SwingEventQueue(), moduleSettings.getName());
		core = coreBuilder.createCore();
		resultCollector = new ResultCollector(core);
		
		core.addConsoleOutputListener(module.getProject().getMessageBus().syncPublisher(InfinitestTopics.CONSOLE_TOPIC));
		core.addDisabledTestListener(module.getProject().getMessageBus().syncPublisher(InfinitestTopics.DISABLED_TEST_TOPIC));
		core.addTestResultsListener(module.getProject().getMessageBus().syncPublisher(InfinitestTopics.TEST_RESULTS_TOPIC));
		
		resultCollector.addChangeListener(module.getProject().getMessageBus().syncPublisher(InfinitestTopics.FAILURE_LIST_TOPIC));
		resultCollector.addStatusChangeListener(module.getProject().getMessageBus().syncPublisher(InfinitestTopics.STATUS_CHANGE_TOPIC));
		resultCollector.addTestQueueListener(module.getProject().getMessageBus().syncPublisher(InfinitestTopics.TEST_QUEUE_TOPIC));
	}

	@Override
	public InfinitestCore getCore() {
		return core;
	}
	
	@Override
	public ResultCollector getResultCollector() {
		return resultCollector;
	}
}
