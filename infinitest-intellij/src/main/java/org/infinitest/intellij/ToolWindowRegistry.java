package org.infinitest.intellij;

import javax.swing.JPanel;

public interface ToolWindowRegistry
{
    void registerToolWindow(JPanel panel, String windowId);

    void unregisterToolWindow(String windowId);
}
