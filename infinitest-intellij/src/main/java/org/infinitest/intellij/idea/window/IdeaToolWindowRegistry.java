package org.infinitest.intellij.idea.window;

import javax.swing.JPanel;

import org.infinitest.intellij.ToolWindowRegistry;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.ui.UIUtil;

public class IdeaToolWindowRegistry implements ToolWindowRegistry
{
    private Project project;

    public IdeaToolWindowRegistry(Project project)
    {
        this.project = project;
    }

    public void registerToolWindow(JPanel panel, String windowId)
    {
        panel.setBackground(UIUtil.getTreeTextBackground());

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow window = toolWindowManager.registerToolWindow(windowId, false, ToolWindowAnchor.BOTTOM);

        IdeaWindowHelper windowHelper = new IdeaWindowHelper();
        windowHelper.addPanelToWindow(panel, window);
    }

    public void unregisterToolWindow(String windowId)
    {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.unregisterToolWindow(windowId);
    }
}
