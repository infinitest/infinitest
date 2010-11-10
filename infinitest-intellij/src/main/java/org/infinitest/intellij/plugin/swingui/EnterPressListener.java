package org.infinitest.intellij.plugin.swingui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTree;

import org.infinitest.testrunner.TestEvent;

class EnterPressListener extends KeyAdapter
{
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            JTree tree = (JTree) e.getSource();
            Object lastPathComponent = tree.getSelectionPath().getLastPathComponent();
            if (lastPathComponent instanceof TestEvent)
            {
                TestEvent event = (TestEvent) lastPathComponent;
                showInfoPane(event);
            }
        }
    }

    protected void showInfoPane(TestEvent event)
    {
        new EventInfoFrame(event).setVisible(true);
    }
}
