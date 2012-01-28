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
