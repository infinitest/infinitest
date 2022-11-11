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
package org.infinitest.eclipse.trim;

import static com.google.common.collect.Sets.*;
import static org.eclipse.swt.SWT.*;
import static org.infinitest.eclipse.workspace.WorkspaceStatusFactory.*;

import java.util.*;

import org.infinitest.*;
import org.infinitest.eclipse.status.*;
import org.infinitest.testrunner.*;
import org.springframework.stereotype.*;

@Component
public class VisualStatusPresenter extends TestQueueAdapter implements VisualStatusRegistry {
	private VisualStatus status;
	private final Set<String> testsRan = newLinkedHashSet();

	@Override
	public void testQueueUpdated(TestQueueEvent event) {
		if (!event.getTestQueue().isEmpty()) {
			statusChanged(runningTests(event.getTestQueue().size(), event.getCurrentTest()));
		} else {
			statusChanged(testRunFinished(testsRan));
		}
	}

	@Override
	public void coreStatusChanged(CoreStatus oldStatus, CoreStatus newStatus) {
		switch (newStatus) {
			case PASSING:
				setPassingColors();
				break;
			case FAILING:
				setFailingColors();
				break;
			default:
				break;
		}
	}

	private void setPassingColors() {
		status.setBackgroundColor(COLOR_DARK_GREEN);
		status.setTextColor(COLOR_WHITE);
	}

	private void setFailingColors() {
		status.setBackgroundColor(ColorSettings.getFailingBackgroundColor());
		status.setTextColor(ColorSettings.getFailingTextColor());
	}

	@Override
	public void updateVisualStatus(VisualStatus status) {
		this.status = status;
	}

	@Override
	public void filesSaved() {
		testsRan.clear();
	}

	@Override
	public void testCaseComplete(TestCaseEvent event) {
		testsRan.add(event.getTestName());
		if (event.failed()) {
			setFailingColors();
			// Remove this test from the set of tests currently being run.
		}

		// Keep in mind that tests can be interrupted before they are completed,
		// so testCaseStarting will be called again before this is called
	}

	@Override
	public void testCaseStarting(TestEvent event) {
		// Add this test to the set of tests currently being run
	}

	@Override
	public void statusChanged(WorkspaceStatus newStatus) {
		status.setText(newStatus.getMessage());
		status.setToolTip(newStatus.getToolTip());
		if (newStatus.warningMessage()) {
			status.setBackgroundColor(COLOR_YELLOW);
			status.setTextColor(COLOR_BLACK);
		}
	}
}