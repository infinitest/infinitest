package org.infinitest.intellij.plugin.swingui.harness;

import static org.infinitest.intellij.plugin.swingui.harness.SwingPanelHarness.*;

import javax.swing.JFrame;

import org.infinitest.intellij.plugin.swingui.InfinitestLogPane;

public class LogPaneHarness extends JFrame
{
    private static final long serialVersionUID = -1L;
    private static final Exception EXCEPTION = new Exception("Uh-oh!");

    public static void main(String args[])
    {
        InfinitestLogPane logPane = new InfinitestLogPane();

        displayPanel(logPane);

        logPane.writeMessage("Something interesting happened");
        logPane.writeMessage("Something else interesting happened");
        logPane.writeError("Oops!", EXCEPTION);
    }
}
