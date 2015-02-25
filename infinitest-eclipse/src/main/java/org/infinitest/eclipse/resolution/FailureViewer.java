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
package org.infinitest.eclipse.resolution;

import static org.eclipse.swt.SWT.*;

import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.infinitest.eclipse.workspace.*;

public class FailureViewer {
	private final List<StackTraceElement> stackTrace;
	private final ResourceLookup resourceFinder;
	private final Shell viewerDialog;
	private final String message;

	public FailureViewer(Shell shell, String message, List<StackTraceElement> stackTrace, ResourceLookup resourceLookup) {
		this.message = message;
		viewerDialog = new Shell(shell, ON_TOP | APPLICATION_MODAL);
		GridLayout gridLayout = new GridLayout(1, true);
		viewerDialog.setLayout(gridLayout);
		this.stackTrace = stackTrace;
		resourceFinder = resourceLookup;
	}

	public Shell show() {
		// RISK Untested
		show(viewerDialog);
		return viewerDialog;
	}

	public void show(final Shell dialog) {
		createMessage(dialog);
		createList(dialog);
		dialog.pack();
		dialog.layout();
		moveToParentShellMonitor(dialog);
		dialog.open();

		dialog.forceActive();
		dialog.addShellListener(new DialogDeactivationDisposer(dialog));
	}

	private void moveToParentShellMonitor(Shell dialog) {
		if (dialog.getParent() == null) {
			// No parent shell no positioning
			return;
		}
		Monitor parentShellMonitor = dialog.getParent().getMonitor();
		Point newPosition = new Point(parentShellMonitor.getBounds().x, parentShellMonitor.getBounds().y);

		// Move preserving size
		dialog.setBounds(newPosition.x, newPosition.y, dialog.getSize().x, dialog.getSize().y);
	}

	private void createMessage(Shell dialog) {
		Group failureMessageGroup = new Group(dialog, SHADOW_ETCHED_IN);
		GridData gridData = new GridData();
		gridData.widthHint = 100;
		gridData.horizontalAlignment = SWT.FILL;
		failureMessageGroup.setLayoutData(gridData);
		failureMessageGroup.setLayout(new FillLayout());
		failureMessageGroup.setText("Failure Message:");
		Label label = new Label(failureMessageGroup, WRAP);
		label.setText(message);
	}

	private void createList(final Shell dialog) {
		org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(dialog, BORDER | V_SCROLL);
		for (StackTraceElement each : stackTrace) {
			list.add(each.toString());
		}
		StackElementSelectionListener selectionListener = new StackElementSelectionListener(dialog, resourceFinder, stackTrace);
		list.addKeyListener(selectionListener);
		list.addMouseListener(selectionListener);
		GridData gridData = new GridData(FILL, FILL, true, true, 1, 1);
		gridData.heightHint = 400;
		list.setLayoutData(gridData);
	}
}
