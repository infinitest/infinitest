package org.infinitest.intellij.plugin.swingui.harness;

import static javax.swing.JFrame.*;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SwingPanelHarness
{
    public static void displayPanel(JPanel pane)
    {
        JFrame frame = new JFrame();

        frame.setTitle("Panel Harness");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(topPanel);

        topPanel.add(pane);

        frame.setVisible(true);
    }
}
