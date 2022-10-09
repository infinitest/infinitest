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

import static java.awt.Color.BLACK;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static org.infinitest.intellij.plugin.launcher.StatusMessages.getMessage;

import java.awt.Color;
import java.util.Collection;

import javax.swing.JComponent;

import org.infinitest.CoreStatus;
import org.infinitest.FailureListListener;
import org.infinitest.StatusChangeListener;
import org.infinitest.TestControl;
import org.infinitest.TestQueueEvent;
import org.infinitest.TestQueueListener;
import org.infinitest.intellij.InfinitestAnnotator;
import org.infinitest.intellij.InfinitestTopics;
import org.infinitest.intellij.idea.ProjectTestControl;
import org.infinitest.intellij.plugin.swingui.HaltTestAction;
import org.infinitest.intellij.plugin.swingui.InfinitestView;
import org.infinitest.intellij.plugin.swingui.ReloadIndexAction;
import org.infinitest.intellij.plugin.swingui.SwingEventQueue;
import org.infinitest.testrunner.TestEvent;
import org.infinitest.util.InfinitestUtils;

import com.intellij.ide.PowerSaveMode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.messages.MessageBusConnection;

public class InfinitestPresenter implements StatusChangeListener, TestQueueListener, FailureListListener, PowerSaveMode.Listener {
	public static final Color PASSING_COLOR = new Color(0x359b35);
	public static final Color FAILING_COLOR = RED;
	public static final Color UNKNOWN_COLOR = YELLOW;

	private final Project project;
	private final InfinitestView view;
	private final StateMonitor monitor;
	private final InfinitestAnnotator annotator;

	public InfinitestPresenter(Project project, InfinitestView infinitestView) {
		this.project = project;
		
		MessageBusConnection connection = project.getMessageBus().connect();
		TestControl control = project.getService(ProjectTestControl.class);
		
		this.annotator = project.getService(InfinitestAnnotator.class);
		view = infinitestView;
		view.addAction(new ReloadIndexAction(project));
		view.addAction(new HaltTestAction(control));
		monitor = new StateMonitor();
		updateStatus();
		indicateWaitingForChanges();
		
		connection.subscribe(InfinitestTopics.STATUS_CHANGE_TOPIC, this);
		connection.subscribe(InfinitestTopics.TEST_QUEUE_TOPIC, this);
		connection.subscribe(InfinitestTopics.FAILURE_LIST_TOPIC, this);
		connection.subscribe(InfinitestTopics.STATUS_CHANGE_TOPIC, monitor);
		connection.subscribe(PowerSaveMode.TOPIC, this);
	}

	private void indicateWaitingForChanges() {
		view.setProgressBarColor(BLACK);
		view.setMaximumProgress(1);
		view.setProgress(1);
	}

	@Override
	public void coreStatusChanged(CoreStatus oldStatus, final CoreStatus newStatus) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				updateView(newStatus);
			}
		};
		new SwingEventQueue().pushAndWait(runnable);
	}

	@Override
	public void testQueueUpdated(TestQueueEvent event) {
		view.setMaximumProgress(event.getInitialSize());
		view.setProgress(1 + event.getTestsRun());
		if (!event.getTestQueue().isEmpty()) {
			view.setCurrentTest(event.getCurrentTest());
		}
	}

	private void updateView(CoreStatus status) {
		view.setStatusMessage(StatusMessages.getMessage(status));
		switch (status) {
			case RUNNING:
				view.setProgressBarColor(UNKNOWN_COLOR);
				break;
			case FAILING:
				view.setProgressBarColor(FAILING_COLOR);
				view.setProgress(view.getMaximumProgress());
				view.setCurrentTest("");
				showBalloon("Failure", MessageType.ERROR, 5000L);
				break;
			case PASSING:
				view.setProgressBarColor(PASSING_COLOR);
				view.setProgress(view.getMaximumProgress());
				view.setCurrentTest("");
				showBalloon("Success", MessageType.INFO, 2000L);
				break;
			case INDEXING:
				view.setProgressBarColor(UNKNOWN_COLOR);
				view.setProgress(view.getMaximumProgress());
				break;
			case SCANNING:
				// No additional updates needed
				break;
			default:
				throw new IllegalArgumentException("Unknown status " + status);
		}
	}

	private void showBalloon(String message, MessageType type, long fadeoutTime) {
		try {
			String html = "<html><body><strong>Infinitest:</strong> " + message + "</body></html>";

			JComponent component = WindowManager.getInstance().getIdeFrame(project).getComponent();
			JBPopupFactory.getInstance()
				.createHtmlTextBalloonBuilder(html, type, null)
				.setFadeoutTime(fadeoutTime)
				.createBalloon()
				.show(RelativePoint.getNorthEastOf(component), Balloon.Position.above);
		} catch (Exception e) {
			// Ignore error. It's just a balloon
		}
	}

	private void updateStatus() {
		long cycleTime = monitor.getCycleLengthInMillis();
		String timeStamp = InfinitestUtils.formatTime(cycleTime);
		view.setAngerBasedOnTime(cycleTime);
		view.setCycleTime(timeStamp);
		view.setStatusMessage(getMessage(CoreStatus.SCANNING));
	}

	@Override
	public void reloading() {
		// nothing to do here
	}

	@Override
	public void testRunComplete() {
		// nothing to do here
	}

	@Override
	public void failureListChanged(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		for (TestEvent added : failuresAdded) {
			annotator.annotate(added);
		}
		for (TestEvent removed : failuresRemoved) {
			annotator.clearAnnotation(removed);
		}
	}

	@Override
	public void failuresUpdated(Collection<TestEvent> updatedFailures) {
		for (TestEvent updated : updatedFailures) {
			annotator.clearAnnotation(updated);
			annotator.annotate(updated);
		}
	}
	
	@Override
	public void powerSaveStateChanged() {
		indicateWaitingForChanges();
		
		String message;
		if (PowerSaveMode.isEnabled()) {
			message = "Infinitst disabled when power save mode is enabled";
		} else {
			message = "Waiting for changes";
		}
		
		view.setStatusMessage(message);
	}
}
