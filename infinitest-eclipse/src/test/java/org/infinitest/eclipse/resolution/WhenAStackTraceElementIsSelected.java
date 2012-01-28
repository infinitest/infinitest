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
package org.infinitest.eclipse.resolution;

import static java.util.Arrays.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.eclipse.swt.SWT.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.*;
import org.infinitest.eclipse.workspace.*;
import org.junit.*;

import com.google.common.collect.*;

public class WhenAStackTraceElementIsSelected {
	private FakeShell shell;
	private KeyEvent keyEvent;
	private StackElementSelectionListener listener;
	private IMarker selectedMarker;
	private IMarker newMarker;
	private List list;
	private ResourceLookup resourceLookup;
	private java.util.List<StackTraceElement> stackTrace;
	private Event event;

	@Before
	public void inContext() {
		shell = new FakeShell();
		stackTrace = Lists.newArrayList();
		stackTrace.add(new StackTraceElement("MyClassName", "someMethod", "MyClassName.java", 72));
		resourceLookup = mock(ResourceLookup.class);
		listener = new StackElementSelectionListener(shell, resourceLookup, stackTrace) {
			@Override
			protected void jumpToMarker(IMarker marker) throws PartInitException {
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
	public void shouldOnlyReactToReturnKeyEvents() {
		keyEvent.keyCode = ARROW_DOWN;
		listener.keyPressed(keyEvent);
		assertFalse(shell.disposed);
	}

	@Test
	public void shouldDoNothingIfNoLineSelected() {
		list.setSelection(-1);
		listener.keyPressed(keyEvent);
		assertFalse(shell.disposed);
	}

	@Test
	public void shouldCloseTheDialog() throws CoreException {
		expectJumpTo("MyClassName", 72);
		listener.keyPressed(keyEvent);
		assertTrue(shell.disposed);
	}

	@Test
	public void shouldJumpToThatLine() throws CoreException {
		expectJumpToTheOtherClass();
		listener.keyPressed(keyEvent);
		assertSame(newMarker, selectedMarker);
	}

	@Test
	public void shouldDoNothingIfFileCannotBeFound() {
		when(resourceLookup.findResourcesForClassName("MissingClass")).thenReturn(Collections.<IResource> emptyList());
		stackTrace.add(new StackTraceElement("MissingClass", "MissingMethod", "MissingClass.java", 72));
		list.setSelection(1);
		listener.keyPressed(keyEvent);
		assertFalse(shell.disposed);
	}

	@Test
	public void shouldJumpToThatLineWhenYouDoubleClickWithTheMouseToo() throws CoreException {
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
