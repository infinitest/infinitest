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
package org.infinitest.intellij;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.infinitest.ConsoleOutputListener;
import org.infinitest.DisabledTestListener;
import org.infinitest.FailureListListener;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestQueueListener;
import org.infinitest.intellij.idea.ProjectTestControl;
import org.infinitest.intellij.plugin.launcher.InfinitestLauncher;
import org.infinitest.testrunner.TestResultsListener;
import org.junit.Before;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;

/**
 * @author gtoison
 *
 */
public class IntellijMockBase {

	protected Project project;
	protected Module module;
	protected ModuleManager moduleManager;
	
	protected MessageBus messageBus;
	protected MessageBusConnection messageBusConnection;
	
	protected InfinitestLauncher launcher;
	protected ProjectTestControl control;
	protected InfinitestAnnotator annotator;
	
	@Before
	public void setupIntellijMockBase() {
		project = mock(Project.class);
		module = mock(Module.class);
		moduleManager = mock(ModuleManager.class);
		messageBus = mock(MessageBus.class);
		messageBusConnection = mock(MessageBusConnection.class);
		launcher = mock(InfinitestLauncher.class);
		control = new ProjectTestControl(project);
		annotator = mock(InfinitestAnnotator.class);
		
		when(project.getComponent(ModuleManager.class)).thenReturn(moduleManager);
		when(project.getMessageBus()).thenReturn(messageBus);
		when(project.getService(ProjectTestControl.class)).thenReturn(control);
		when(project.getService(InfinitestAnnotator.class)).thenReturn(annotator);
		
		when(module.getName()).thenReturn("module");
		when(module.getProject()).thenReturn(project);
		when(module.getService(InfinitestLauncher.class)).thenReturn(launcher);
		
		when(moduleManager.getModules()).thenReturn(new Module[] {module});
		
		when(messageBus.connect()).thenReturn(messageBusConnection);
		
		when(messageBus.syncPublisher(InfinitestTopics.CONSOLE_TOPIC)).thenReturn(mock(ConsoleOutputListener.class));
		when(messageBus.syncPublisher(InfinitestTopics.DISABLED_TEST_TOPIC)).thenReturn(mock(DisabledTestListener.class));
		when(messageBus.syncPublisher(InfinitestTopics.FAILURE_LIST_TOPIC)).thenReturn(mock(FailureListListener.class));
		when(messageBus.syncPublisher(InfinitestTopics.STATUS_CHANGE_TOPIC)).thenReturn(mock(StatusChangeListener.class));
		when(messageBus.syncPublisher(InfinitestTopics.TEST_QUEUE_TOPIC)).thenReturn(mock(TestQueueListener.class));
		when(messageBus.syncPublisher(InfinitestTopics.TEST_RESULTS_TOPIC)).thenReturn(mock(TestResultsListener.class));
	}
	
	/**
	 * Not executed automatically since most tests don't need it, this needs to be called explicitly
	 */
	public static void setupApplication() {
		Application application = mock(Application.class);
		Disposable parent = mock(Disposable.class);
		
		// Setup the console factory
		TextConsoleBuilder builder = mock(TextConsoleBuilder.class);
		ConsoleView consoleView = mock(ConsoleView.class);
		
		TextConsoleBuilderFactory consoleBuilderFactory = mock(TextConsoleBuilderFactory.class);
		
		when(application.getService(TextConsoleBuilderFactory.class)).thenReturn(consoleBuilderFactory);
		when(consoleBuilderFactory.createBuilder(any())).thenReturn(builder);
		when(builder.getConsole()).thenReturn(consoleView);

		// Setup the content factory
		ContentFactory contentFactory = mock(ContentFactory.class);

		when(application.getService(ContentFactory.class)).thenReturn(contentFactory);
		
		ApplicationManager.setApplication(application, parent);
	}
}
