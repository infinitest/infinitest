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

import static com.google.common.collect.Lists.*;
import static org.eclipse.swt.SWT.*;
import static org.junit.Assert.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.infinitest.eclipse.workspace.FakeResourceFinder;
import org.junit.Before;
import org.junit.Test;

public class WhenViewingAStackTrace
{
    private List list;
    private StackTraceElement element;
    private FailureViewer view;
    private FakeShell dialog;

    @Before
    public void inContext()
    {
        element = new StackTraceElement("class1", "method1", "file1", 0);
        view = new FailureViewer(null, "message", newArrayList(element), null);
        dialog = new FakeShell();
        view.show(dialog);
        list = (List) dialog.getChildren()[1];
    }

    @Test
    public void shouldBuildListItemsFromStackTrace()
    {
        assertEquals(1, list.getItemCount());
        assertEquals(element.toString(), list.getItem(0));
    }

    @Test
    public void shouldUseSingleSelection()
    {
        assertTrue((list.getStyle() & SWT.MULTI) == 0);
    }

    @Test
    public void shouldDisposeTheDialogWhenTheUserClicksOffTheViewer()
    {
        Listener[] listeners = dialog.getListeners(Deactivate);
        assertEquals(1, listeners.length);
    }

    @Test
    public void shouldRespondToKeyEvents()
    {
        assertEquals(1, list.getListeners(KeyDown).length);
    }

    @Test
    public void shouldShowTheNavigatorAndTakeFocus()
    {
        assertTrue(dialog.opened);
        assertTrue(dialog.active);
        assertTrue(dialog.packed);
        assertTrue(dialog.layout);
    }

    /**
     * Presentation test harness
     */
    public static void main(String... args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Fake Eclipse");
        shell.setSize(300, 200);
        shell.open();

        Throwable throwable = buildAFakeStackTraceRecursively(100);
        String msg = "Hello, this is a really long error message that is supposed to demonstrate how well we can"
                        + " wrap long error messages. Evidently, they need to be long than what I've typed here";
        Shell dialog = new FailureViewer(shell, msg, newArrayList(throwable.getStackTrace()), new FakeResourceFinder())
                        .show();
        while (!shell.isDisposed() && !dialog.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        display.dispose();
    }

    private static Throwable buildAFakeStackTraceRecursively(int i)
    {
        if (i == 0)
        {
            Throwable throwable = new Throwable();
            throwable.fillInStackTrace();
            return throwable;
        }
        return buildAFakeStackTraceRecursively(i - 1);
    }
}
