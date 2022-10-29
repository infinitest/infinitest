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

import static java.util.Arrays.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.eclipse.swt.SWT.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;
import org.infinitest.eclipse.workspace.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenAStackTraceElementIsSelected {
	private FakeShell shell;
	private KeyEvent keyEvent;
	private StackElementSelectionListener listener;
	private IMarker selectedMarker;
	private IMarker newMarker;
	private List list;
	private ResourceLookup resourceLookup;
	private java.util.List<StackTraceElement> stackTrace;
	private Event event;

	@BeforeEach
	void inContext() {
		shell = new FakeShell();
		stackTrace = new ArrayList<>();
		stackTrace.add(new StackTraceElement("MyClassName", "someMethod", "MyClassName.java", 72));
		resourceLookup = mock(ResourceLookup.class);
		listener = new StackElementSelectionListener(shell, resourceLookup, stackTrace) {
			@Override
			protected void jumpToMarker(IMarker marker) {
				selectedMarker = marker;
			}
		};
		event = new Event();
		list = new List(shell, 0);
		list.add("Entry 1");
		list.add("Entry 2");
		list.setSelection(0);
		event.widget = list;
		keyEvent = new KeyEvent(event);
		keyEvent.keyCode = SWT.CR;
	}

	private void expectJumpTo(String className, int lineNumber) throws CoreException {
		IResource resource = mock(IResource.class);
		when(resourceLookup.findResourcesForClassName(className)).thenReturn(asList(resource));
		newMarker = mock(IMarker.class);
		IFile file = mock(IFile.class);
		when(resource.getAdapter(IFile.class)).thenReturn(file);
		when(file.createMarker(TEXT)).thenReturn(newMarker);
		newMarker.setAttribute(LINE_NUMBER, lineNumber);
		newMarker.delete();
	}

	@Test
	void shouldOnlyReactToReturnKeyEvents() {
		keyEvent.keyCode = ARROW_DOWN;
		listener.keyPressed(keyEvent);
		assertFalse(shell.disposed);
	}

	@Test
	void shouldDoNothingIfNoLineSelected() {
		list.setSelection(-1);
		listener.keyPressed(keyEvent);
		assertFalse(shell.disposed);
	}

	@Test
	void shouldCloseTheDialog() throws CoreException {
		expectJumpTo("MyClassName", 72);
		listener.keyPressed(keyEvent);
		assertTrue(shell.disposed);
	}

	@Test
	void shouldJumpToThatLine() throws CoreException {
		expectJumpToTheOtherClass();
		listener.keyPressed(keyEvent);
		assertSame(newMarker, selectedMarker);
	}

	@Test
	void shouldDoNothingIfFileCannotBeFound() {
		when(resourceLookup.findResourcesForClassName("MissingClass")).thenReturn(Collections.<IResource> emptyList());
		stackTrace.add(new StackTraceElement("MissingClass", "MissingMethod", "MissingClass.java", 72));
		list.setSelection(1);
		listener.keyPressed(keyEvent);
		assertFalse(shell.disposed);
	}

	@Test
	void shouldJumpToThatLineWhenYouDoubleClickWithTheMouseToo() throws CoreException {
		expectJumpToTheOtherClass();
		listener.mouseDoubleClick(new MouseEvent(event));
		assertSame(newMarker, selectedMarker);
	}

	private void expectJumpToTheOtherClass() throws CoreException {
		expectJumpTo("TheOtherClass", 21);
		stackTrace.add(new StackTraceElement("TheOtherClass", "", "TheOtherClass.java", 21));
		list.add("This also doesn't matter");
		list.setSelection(1);
	}
}
