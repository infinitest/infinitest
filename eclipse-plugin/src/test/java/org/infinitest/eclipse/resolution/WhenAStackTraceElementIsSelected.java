package org.infinitest.eclipse.resolution;

import static java.util.Arrays.*;
import static org.easymock.EasyMock.*;
import static org.eclipse.core.resources.IMarker.*;
import static org.eclipse.swt.SWT.*;
import static org.junit.Assert.*;

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PartInitException;
import org.infinitest.eclipse.workspace.ResourceLookup;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class WhenAStackTraceElementIsSelected
{
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
    public void inContext()
    {
        shell = new FakeShell();
        stackTrace = Lists.newArrayList();
        stackTrace.add(new StackTraceElement("MyClassName", "someMethod", "MyClassName.java", 72));
        resourceLookup = createMock(ResourceLookup.class);
        listener = new StackElementSelectionListener(shell, resourceLookup, stackTrace)
        {
            @Override
            protected void jumpToMarker(IMarker marker) throws PartInitException
            {
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

    private void expectJumpTo(String className, int lineNumber) throws CoreException
    {
        IResource resource = createMock(IResource.class);
        expect(resourceLookup.findResourcesForClassName(className)).andReturn(asList(resource));
        newMarker = createMock(IMarker.class);
        IFile file = createMock(IFile.class);
        expect(resource.getAdapter(IFile.class)).andReturn(file);
        expect(file.createMarker(TEXT)).andReturn(newMarker);
        newMarker.setAttribute(LINE_NUMBER, lineNumber);
        newMarker.delete();
        replay(newMarker, resource, file, resourceLookup);
    }

    @Test
    public void shouldOnlyReactToReturnKeyEvents()
    {
        keyEvent.keyCode = ARROW_DOWN;
        listener.keyPressed(keyEvent);
        assertFalse(shell.disposed);
    }

    @Test
    public void shouldDoNothingIfNoLineSelected()
    {
        list.setSelection(-1);
        listener.keyPressed(keyEvent);
        assertFalse(shell.disposed);
    }

    @Test
    public void shouldCloseTheDialog() throws CoreException
    {
        expectJumpTo("MyClassName", 72);
        listener.keyPressed(keyEvent);
        assertTrue(shell.disposed);
    }

    @Test
    public void shouldJumpToThatLine() throws CoreException
    {
        expectJumpToTheOtherClass();
        listener.keyPressed(keyEvent);
        assertSame(newMarker, selectedMarker);
    }

    @Test
    public void shouldDoNothingIfFileCannotBeFound()
    {
        expect(resourceLookup.findResourcesForClassName("MissingClass")).andReturn(Collections.<IResource> emptyList());
        replay(resourceLookup);
        stackTrace.add(new StackTraceElement("MissingClass", "MissingMethod", "MissingClass.java", 72));
        list.setSelection(1);
        listener.keyPressed(keyEvent);
        assertFalse(shell.disposed);
    }

    @Test
    public void shouldJumpToThatLineWhenYouDoubleClickWithTheMouseToo() throws CoreException
    {
        expectJumpToTheOtherClass();
        listener.mouseDoubleClick(new MouseEvent(event));
        assertSame(newMarker, selectedMarker);
    }

    private void expectJumpToTheOtherClass() throws CoreException
    {
        expectJumpTo("TheOtherClass", 21);
        stackTrace.add(new StackTraceElement("TheOtherClass", "", "TheOtherClass.java", 21));
        list.add("This also doesn't matter");
        list.setSelection(1);
    }
}
