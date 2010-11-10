package org.infinitest.intellij.plugin.swingui;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class TreeFocusListener extends WindowAdapter
{
    @Override
    public void windowGainedFocus(WindowEvent e)
    {
        InfinitestMainFrame mainFrame = (InfinitestMainFrame) e.getSource();
        setFocus(mainFrame.getTree());
    }

    protected void setFocus(Component c)
    {
        c.requestFocusInWindow();
    }
}
