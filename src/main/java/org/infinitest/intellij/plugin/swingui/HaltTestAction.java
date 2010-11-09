package org.infinitest.intellij.plugin.swingui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.infinitest.TestControl;

public class HaltTestAction extends AbstractAction
{
    private static final long serialVersionUID = -1L;

    private final ImageIcon stopIcon;
    private final ImageIcon startIcon;
    private final TestControl control;

    public HaltTestAction(TestControl testControl)
    {
        control = testControl;
        stopIcon = new ImageIcon(HaltTestAction.class.getResource("stop.png"));
        startIcon = new ImageIcon(HaltTestAction.class.getResource("rerun.png"));
        setIcon();
    }

    private void setIcon()
    {
        if (control.shouldRunTests())
        {
            putValue(Action.SMALL_ICON, stopIcon);
            putValue(Action.SHORT_DESCRIPTION, "Stop running the tests");
        }
        else
        {
            putValue(Action.SMALL_ICON, startIcon);
            putValue(Action.SHORT_DESCRIPTION, "Start running the tests");
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        control.setRunTests(!control.shouldRunTests());
        setIcon();
    }
}
