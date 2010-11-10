package org.infinitest.intellij.plugin.swingui.harness;

import static org.infinitest.intellij.plugin.swingui.harness.SwingPanelHarness.*;

import javax.swing.JFrame;

import org.infinitest.intellij.plugin.swingui.ConfigurationPane;

public class FacetPaneHarness extends JFrame
{
    private static final long serialVersionUID = -1L;

    public static void main(String args[])
    {
        displayPanel(new ConfigurationPane());
    }
}
