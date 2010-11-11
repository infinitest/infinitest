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

import static org.eclipse.core.resources.IMarker.*;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.infinitest.eclipse.workspace.ResourceLookup;

class StackElementSelectionListener extends KeyAdapter implements MouseListener
{
    private final Shell dialog;
    private final ResourceLookup resourceLookup;
    private final List<StackTraceElement> stackTrace;

    StackElementSelectionListener(Shell dialog, ResourceLookup resourceLookup, List<StackTraceElement> stackTrace)
    {
        this.dialog = dialog;
        this.resourceLookup = resourceLookup;
        this.stackTrace = stackTrace;
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) event.widget;
        if (event.keyCode == SWT.CR)
        {
            jumpToSelectedLine(list);
        }
    }

    private void jumpToSelectedLine(org.eclipse.swt.widgets.List list)
    {
        if (list.getSelectionCount() > 0)
        {
            StackTraceElement element = stackTrace.get(list.getSelectionIndex());
            IFile classFile = getClassFile(element.getClassName());
            if (classFile != null)
            {
                jumpAndCloseDialog(element, classFile);
            }
        }
    }

    private void jumpAndCloseDialog(StackTraceElement element, IFile classFile)
    {
        jumpToLine(classFile, element.getLineNumber());
        dialog.dispose();
    }

    private IFile getClassFile(String className)
    {
        List<IResource> resources = resourceLookup.findResourcesForClassName(className);
        if (resources.isEmpty())
        {
            return null;
        }
        return (IFile) resources.get(0).getAdapter(IFile.class);
    }

    private void jumpToLine(IFile classfile, int lineNumber)
    {
        try
        {
            IMarker marker = classfile.createMarker(TEXT);
            marker.setAttribute(LINE_NUMBER, lineNumber);
            jumpToMarker(marker);
            marker.delete();
        }
        catch (PartInitException e)
        {
            throw new RuntimeException(e);
        }
        catch (CoreException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected void jumpToMarker(IMarker marker) throws CoreException
    {
        // RISK Untested
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IDE.openEditor(page, marker);
    }

    public void mouseDoubleClick(MouseEvent event)
    {
        jumpToSelectedLine((org.eclipse.swt.widgets.List) event.widget);
    }

    public void mouseDown(MouseEvent arg0)
    {
    }

    public void mouseUp(MouseEvent arg0)
    {
    }
}