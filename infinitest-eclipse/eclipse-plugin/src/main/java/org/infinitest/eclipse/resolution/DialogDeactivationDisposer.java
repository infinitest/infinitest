package org.infinitest.eclipse.resolution;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Shell;

final class DialogDeactivationDisposer extends ShellAdapter
{
    private final Shell dialog;

    DialogDeactivationDisposer(Shell dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void shellDeactivated(ShellEvent arg0)
    {
        dialog.dispose();
    }
}