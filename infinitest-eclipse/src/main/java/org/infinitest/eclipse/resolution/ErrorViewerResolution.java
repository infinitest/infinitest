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

import static com.google.common.collect.Lists.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.infinitest.eclipse.markers.ProblemMarkerInfo.*;
import static org.infinitest.eclipse.util.PickleJar.*;

import java.io.*;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.infinitest.eclipse.*;
import org.infinitest.eclipse.workspace.*;

public class ErrorViewerResolution implements IMarkerResolution2 {
	private final String name;
	private final StackTraceFilter stackTraceFilter;

	public ErrorViewerResolution(String testAndMethodName) {
		name = testAndMethodName;
		stackTraceFilter = new StackTraceFilter();
	}

	@Override
	public String getDescription() {
		return "Shows the details for this test failure";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return name + " failing (see details)";
	}

	@Override
	public void run(IMarker marker) {
		try {
			Serializable unpickledStack = unpickle(marker.getAttribute(PICKLED_STACK_TRACE_ATTRIBUTE).toString());
			List<StackTraceElement> stackTrace = newArrayList((StackTraceElement[]) unpickledStack);
			createStackViewWith(stackTrace, marker.getAttribute(MESSAGE).toString());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	protected void createStackViewWith(List<StackTraceElement> stackTrace, String message) {
		List<StackTraceElement> filteredStackTrace = stackTraceFilter.filterStack(stackTrace);
		ResourceLookup resourceLookup = InfinitestPlugin.getInstance().getBean(ResourceLookup.class);
		new FailureViewer(getMainShell(), message, filteredStackTrace, resourceLookup).show();
	}

	private Shell getMainShell() {
		// RISK Untested
		for (Shell shell : Display.getDefault().getShells()) {
			if (isPrimary(shell)) {
				return shell;
			}
		}
		// This is problematic because the active shell may be the Problems View
		// QuickFix dialog,
		// rather than the main eclipse shell
		return Display.getDefault().getActiveShell();
	}

	private boolean isPrimary(Shell shell) {
		// This is my best guess at how to identify the primary shell
		return shell.getParent() == null;
	}
}
