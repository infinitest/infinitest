package org.infinitest.intellij;

import javax.swing.JPanel;

@SuppressWarnings("all")
public class FakeToolWindowRegistry implements ToolWindowRegistry
{
    public void registerToolWindow(JPanel panel, String windowId)
    {
    }

    public void unregisterToolWindow(String windowId)
    {
    }
}
