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

import static org.eclipse.core.resources.IMarker.*;

import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.*;
import org.infinitest.eclipse.workspace.*;

class StackElementSelectionListener extends KeyAdapter implements MouseListener {
	private final Shell dialog;
	private final ResourceLookup resourceLookup;
	private final List<StackTraceElement> stackTrace;

	StackElementSelectionListener(Shell dialog, ResourceLookup resourceLookup, List<StackTraceElement> stackTrace) {
		this.dialog = dialog;
		this.resourceLookup = resourceLookup;
		this.stackTrace = stackTrace;
	}

	@Override
	public void keyPressed(KeyEvent event) {
		org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) event.widget;
		if (event.keyCode == SWT.CR) {
			jumpToSelectedLine(list);
		}
	}

	private void jumpToSelectedLine(org.eclipse.swt.widgets.List list) {
		if (list.getSelectionCount() > 0) {
			StackTraceElement element = stackTrace.get(list.getSelectionIndex());
			IFile classFile = getClassFile(element.getClassName());
			if (classFile != null) {
				jumpAndCloseDialog(element, classFile);
			}
		}
	}

	private void jumpAndCloseDialog(StackTraceElement element, IFile classFile) {
		jumpToLine(classFile, element.getLineNumber());
		dialog.dispose();
	}

	private IFile getClassFile(String className) {
		List<IResource> resources = resourceLookup.findResourcesForClassName(className);
		if (resources.isEmpty()) {
			return null;
		}
		return (IFile) resources.get(0).getAdapter(IFile.class);
	}

	private void jumpToLine(IFile classfile, int lineNumber) {
		try {
			IMarker marker = classfile.createMarker(TEXT);
			marker.setAttribute(LINE_NUMBER, lineNumber);
			jumpToMarker(marker);
			marker.delete();
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	protected void jumpToMarker(IMarker marker) throws CoreException {
		// RISK Untested
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IDE.openEditor(page, marker);
	}

	@Override
	public void mouseDoubleClick(MouseEvent event) {
		jumpToSelectedLine((org.eclipse.swt.widgets.List) event.widget);
	}

	@Override
	public void mouseDown(MouseEvent arg0) {
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
	}
}