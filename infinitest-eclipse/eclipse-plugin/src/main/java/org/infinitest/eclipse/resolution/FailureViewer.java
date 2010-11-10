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
