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

import static org.eclipse.swt.SWT.*;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.infinitest.eclipse.workspace.ResourceLookup;

public class FailureViewer
{
    private final List<StackTraceElement> stackTrace;
    private final ResourceLookup resourceFinder;
    private final Shell viewerDialog;
    private final String message;

    public FailureViewer(Shell shell, String message, List<StackTraceElement> stackTrace, ResourceLookup resourceLookup)
    {
        this.message = message;
        viewerDialog = new Shell(shell, ON_TOP | APPLICATION_MODAL);
        GridLayout gridLayout = new GridLayout(1, true);
        viewerDialog.setLayout(gridLayout);
        this.stackTrace = stackTrace;
        this.resourceFinder = resourceLookup;
    }

    public Shell show()
    {
        // RISK Untested
        show(viewerDialog);
        return viewerDialog;
    }

    public void show(final Shell dialog)
    {
        createMessage(dialog);
        createList(dialog);
        dialog.pack();
        dialog.layout();
        dialog.open();
        dialog.forceActive();
        dialog.addShellListener(new DialogDeactivationDisposer(dialog));
    }

    private void createMessage(Shell dialog)
    {
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

    private void createList(final Shell dialog)
    {
        org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(dialog, BORDER | V_SCROLL);
        for (StackTraceElement each : stackTrace)
        {
            list.add(each.toString());
        }
        StackElementSelectionListener selectionListener = new StackElementSelectionListener(dialog, resourceFinder,
                        stackTrace);
        list.addKeyListener(selectionListener);
        list.addMouseListener(selectionListener);
        GridData gridData = new GridData(FILL, FILL, true, true, 1, 1);
        gridData.heightHint = 400;
        list.setLayoutData(gridData);
    }
}
