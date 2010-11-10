package org.infinitest.eclipse.trim;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;

public class MouseToSelectionAdapter extends MouseAdapter
{
    private final SelectionListener listener;

    public MouseToSelectionAdapter(SelectionListener listener)
    {
        this.listener = listener;
    }

    public void mouseUp(MouseEvent arg0)
    {
        listener.widgetSelected(null);
    }
}
