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

import static org.infinitest.intellij.idea.window.InfinitestToolWindow.*;

import java.awt.*;

import javax.swing.*;

import org.apache.log4j.*;
import org.infinitest.*;
import org.infinitest.intellij.*;
import org.infinitest.intellij.idea.*;
import org.infinitest.intellij.plugin.*;
import org.infinitest.intellij.plugin.greenhook.*;
import org.infinitest.intellij.plugin.swingui.*;
import org.infinitest.util.*;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.wm.*;

public class InfinitestLauncherImpl implements InfinitestLauncher {
	private final ModuleSettings moduleSettings;
	private final ToolWindowRegistry toolWindowRegistry;
	private final CompilationNotifier compilationNotifier;
	private final SourceNavigator navigator;
	private final InfinitestBuilder infinitestBuilder;
	private IdeaCompilationListener testControl;
	private final GreenHookListener greenHookListener;
	private final FileEditorListener fileEditorListener;
	private final ToolWindowListener toolWindowListener;

	public InfinitestLauncherImpl(ModuleSettings moduleSettings, ToolWindowRegistry toolWindowRegistry, CompilationNotifier compilationNotifier, SourceNavigator navigator, FileEditorManager fileEditorManager, ToolWindowManager toolWindowManager) {
		this.moduleSettings = moduleSettings;
		this.toolWindowRegistry = toolWindowRegistry;
		this.compilationNotifier = compilationNotifier;
		this.navigator = navigator;
		greenHookListener = new GreenHookListener();
		infinitestBuilder = createInfinitestBuilder();
		fileEditorListener = new FileEditorListener(fileEditorManager);
		toolWindowListener = new ToolWindowListener(toolWindowManager, toolWindowId());
	}

	@Override
	public void launchInfinitest() {
		moduleSettings.writeToLogger(Logger.getLogger(getClass()));

		testControl = new IdeaCompilationListener(infinitestBuilder.getCore(), moduleSettings);
		initializeInfinitestLogging();
		registerInfinitestWindow();
		addCompilationStatusListener();
		addScmGreenHookListener();
		addResultClickListener();
		addFileEditorListener();
		addToolWindowListener();
	}

	private void addResultClickListener() {
		infinitestBuilder.addResultClickListener(new ResultClickListener(navigator));
	}

	public void addGreenHook(GreenHook hook) {
		greenHookListener.add(hook);
	}

	private void initializeInfinitestLogging() {
		InfinitestUtils.addLoggingListener(new InfinitestLoggingListener(infinitestBuilder.getView()));
	}

	private void addCompilationStatusListener() {
		compilationNotifier.addCompilationStatusListener(testControl);
	}

	private void addScmGreenHookListener() {
		infinitestBuilder.addStatusListener(greenHookListener);
	}

	private void registerInfinitestWindow() {
		JPanel rootPanel = new JPanel(new BorderLayout());

		rootPanel.add(infinitestBuilder.createPluginComponent(testControl), BorderLayout.CENTER);

		toolWindowRegistry.registerToolWindow(rootPanel, toolWindowId());
	}

	@Override
	public void stop() {
		toolWindowRegistry.unregisterToolWindow(toolWindowId());
		infinitestBuilder.removeStatusListener(greenHookListener);
		compilationNotifier.removeCompilationStatusListener(testControl);
	}

	private String toolWindowId() {
		return TOOL_WINDOW_ID + "_" + moduleSettings.getName();
	}

	private InfinitestBuilder createInfinitestBuilder() {
		InfinitestCoreBuilder coreBuilder = new InfinitestCoreBuilder(moduleSettings.getRuntimeEnvironment(), new SwingEventQueue());
		return new InfinitestBuilder(coreBuilder.createCore());
	}

	private void addFileEditorListener() {
		infinitestBuilder.addPresenterListener(fileEditorListener);
	}

	private void addToolWindowListener() {
		infinitestBuilder.addPresenterListener(toolWindowListener);
	}
}
