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

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.infinitest.InfinitestCoreBuilder;
import org.infinitest.intellij.CompilationNotifier;
import org.infinitest.intellij.InfinitestLoggingListener;
import org.infinitest.intellij.ModuleSettings;
import org.infinitest.intellij.ToolWindowRegistry;
import org.infinitest.intellij.idea.IdeaCompilationListener;
import org.infinitest.intellij.plugin.SourceNavigator;
import org.infinitest.intellij.plugin.swingui.ResultClickListener;
import org.infinitest.intellij.plugin.swingui.SwingEventQueue;
import org.infinitest.util.InfinitestUtils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

public class InfinitestLauncherImpl implements InfinitestLauncher {
	private final ModuleSettings moduleSettings;
	private final ToolWindowRegistry toolWindowRegistry;
	private final CompilationNotifier compilationNotifier;
	private final SourceNavigator navigator;
	private final InfinitestBuilder infinitestBuilder;
	private IdeaCompilationListener testControl;
	private final FileEditorListener fileEditorListener;
	private final ToolWindowListener toolWindowListener;
	private final Project project;

	public InfinitestLauncherImpl(ModuleSettings moduleSettings, ToolWindowRegistry toolWindowRegistry, CompilationNotifier compilationNotifier, SourceNavigator navigator, FileEditorManager fileEditorManager, ToolWindowManager toolWindowManager, Project project) {
		this.moduleSettings = moduleSettings;
		this.toolWindowRegistry = toolWindowRegistry;
		this.compilationNotifier = compilationNotifier;
		this.navigator = navigator;
		this.project = project;
		
		infinitestBuilder = createInfinitestBuilder();
		fileEditorListener = new FileEditorListener(fileEditorManager);
		toolWindowListener = new ToolWindowListener(toolWindowManager, toolWindowId());
	}

	@Override
	public void launchInfinitest() {
		moduleSettings.writeToLogger(Logger.getFactory().getLoggerInstance(getClass().getName()));

		testControl = new IdeaCompilationListener(infinitestBuilder.getCore(), moduleSettings);
		initializeInfinitestLogging();
		registerInfinitestWindow();
		addCompilationStatusListener();
		addResultClickListener();
		addFileEditorListener();
		addToolWindowListener();
	}

	private void addResultClickListener() {
		infinitestBuilder.addResultClickListener(new ResultClickListener(navigator));
	}

	private void initializeInfinitestLogging() {
		InfinitestUtils.addLoggingListener(new InfinitestLoggingListener(infinitestBuilder.getView()));
	}

	private void addCompilationStatusListener() {
		compilationNotifier.addCompilationStatusListener(testControl);
	}

	private void registerInfinitestWindow() {
		JPanel rootPanel = new JPanel(new BorderLayout());

		rootPanel.add(infinitestBuilder.createPluginComponent(testControl), BorderLayout.CENTER);

		toolWindowRegistry.registerToolWindow(rootPanel, toolWindowId());
	}

	@Override
	public void stop() {
		toolWindowRegistry.unregisterToolWindow(toolWindowId());
		compilationNotifier.removeCompilationStatusListener(testControl);
	}

	private String toolWindowId() {
		return moduleSettings.getName();
	}

	private InfinitestBuilder createInfinitestBuilder() {
		InfinitestCoreBuilder coreBuilder = new InfinitestCoreBuilder(moduleSettings.getRuntimeEnvironment(), new SwingEventQueue());
		return new InfinitestBuilder(coreBuilder.createCore(), project);
	}

	private void addFileEditorListener() {
		infinitestBuilder.addPresenterListener(fileEditorListener);
	}

	private void addToolWindowListener() {
		infinitestBuilder.addPresenterListener(toolWindowListener);
	}
}
