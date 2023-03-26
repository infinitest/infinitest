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

import org.infinitest.CoreStatus;
import org.infinitest.StatusChangeListener;
import org.infinitest.intellij.InfinitestIcons;
import org.infinitest.intellij.InfinitestLoggingListener;
import org.infinitest.intellij.InfinitestTopics;
import org.infinitest.intellij.idea.IdeaSourceNavigator;
import org.infinitest.intellij.plugin.launcher.FileEditorListener;
import org.infinitest.intellij.plugin.launcher.InfinitestPresenter;
import org.infinitest.intellij.plugin.swingui.InfinitestMainFrame;
import org.infinitest.intellij.plugin.swingui.ResultClickListener;
import org.infinitest.intellij.plugin.swingui.TreeModelAdapter;
import org.infinitest.util.InfinitestUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;

public class InfinitestToolWindowFactory implements ToolWindowFactory {
	public static final String WAITING_ICON_PATH = "/infinitest-waiting.png";
	public static final String RUNNING_ICON_PATH = "/infinitest.png";
	public static final String SUCCESS_ICON_PATH = "/infinitest-success.png";
	public static final String FAILURE_ICON_PATH = "/infinitest-failure.png";

	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		Application application = ApplicationManager.getApplication();
		InfinitestMainFrame frame = new InfinitestMainFrame(project, application);
		
		Content resultsContent = ContentFactory.SERVICE.getInstance().createContent(frame.getResultsPane(), "Results", false);
		Content loggingContent = ContentFactory.SERVICE.getInstance().createContent(frame.getLogPane(), "Logging", false);
		Content consoleContent = ContentFactory.SERVICE.getInstance().createContent(frame.getConsoleFrame().getComponent(), "Console", false);
		
		toolWindow.getContentManager().addContent(resultsContent);
		toolWindow.getContentManager().addContent(loggingContent);
		toolWindow.getContentManager().addContent(consoleContent);
		
		toolWindow.setIcon(IconLoader.getIcon(WAITING_ICON_PATH, getClass()));
		
		FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
		FileEditorListener fileEditorListener = new FileEditorListener(fileEditorManager);
		
		IdeaSourceNavigator navigator = new IdeaSourceNavigator(project);
		ResultClickListener resultClickListener = new ResultClickListener(navigator);
		frame.addResultClickListener(resultClickListener);
		
		TreeModelAdapter treeModelAdapter = new TreeModelAdapter(project);
		frame.setResultsModel(treeModelAdapter);
		
		MessageBusConnection projectMessageBusConnection = project.getMessageBus().connect();
		
		InfinitestToolWindowStatusChangeListener statusChangeListener = new InfinitestToolWindowStatusChangeListener(toolWindow);
		
		projectMessageBusConnection.subscribe(InfinitestTopics.STATUS_CHANGE_TOPIC, statusChangeListener);
		projectMessageBusConnection.subscribe(InfinitestTopics.TEST_QUEUE_TOPIC, fileEditorListener);
		projectMessageBusConnection.subscribe(InfinitestTopics.FAILURE_LIST_TOPIC, treeModelAdapter);
		projectMessageBusConnection.subscribe(InfinitestTopics.CONSOLE_TOPIC, frame);
		projectMessageBusConnection.subscribe(ProjectTopics.MODULES, treeModelAdapter);
		projectMessageBusConnection.subscribe(ProjectTopics.PROJECT_ROOTS, treeModelAdapter);

		new InfinitestPresenter(project, frame);
		
		// TODO replace by something not static
		InfinitestLoggingListener loggingListener = new InfinitestLoggingListener(frame);
		InfinitestUtils.addLoggingListener(loggingListener);
	}
	
	private static class InfinitestToolWindowStatusChangeListener implements StatusChangeListener {
		private final ToolWindow toolWindow;
		
		private InfinitestToolWindowStatusChangeListener(ToolWindow toolWindow) {
			this.toolWindow = toolWindow;
		}
		
		@Override
		public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus) {
			toolWindow.setIcon(InfinitestIcons.getIcon(newStatus));
		}
	}
}
